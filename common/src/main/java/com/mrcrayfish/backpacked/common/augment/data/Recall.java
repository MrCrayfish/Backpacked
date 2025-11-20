package com.mrcrayfish.backpacked.common.augment.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class Recall extends SavedData
{
    public static final String ID = "backpacked_recall";
    private static final int MAX_QUEUE_SIZE = 16;

    private final ServerLevel level;
    private final Map<UUID, Shelf> shelves = new HashMap<>();
    private int timer;
    private boolean runNow;
    private boolean force;

    @SuppressWarnings("DataFlowIssue")
    public static Factory<Recall> factory(ServerLevel level)
    {
        return new Factory<>(() -> new Recall(level), (tag, provider) -> load(level, provider, tag), null);
    }

    public Recall(ServerLevel level)
    {
        this.level = level;
    }

    public void onShelfLoaded(ShelfBlockEntity shelf)
    {
        if(!this.shelves.containsKey(shelf.id()))
        {
            this.shelves.put(shelf.id(), new Shelf(shelf.getBlockPos()));
            this.setDirty();
        }
    }

    public void onShelfBroken(ShelfBlockEntity shelf)
    {
        this.shelves.remove(shelf.id());
        this.removeAndFlushQueueToBlockPos(shelf.id(), shelf.getBlockPos());
        this.setDirty();
    }

    @Nullable
    public BlockPos getShelfBlockPos(UUID shelfId)
    {
        Shelf shelf = this.shelves.get(shelfId);
        return shelf != null ? shelf.pos : null;
    }

    public boolean recallToShelf(ServerPlayer player, UUID shelfId, ItemStack backpack)
    {
        Shelf shelf = this.shelves.get(shelfId);
        if(shelf != null)
        {
            if(!shelf.queues(player, backpack, this.timer))
                return false;
            this.runNow = true;
            this.setDirty();
            return true;
        }
        return false;
    }

    public void forceNextRun()
    {
        this.force = true;
        this.runNow = true;
    }

    public int flushAllQueues(MinecraftServer server)
    {
        int[] count = {0};
        var it = this.shelves.entrySet().iterator();
        while(it.hasNext())
        {
            Shelf shelf = it.next().getValue();
            shelf.forEachQueue((owner, items) -> {
                count[0] += this.flushQueue(server, owner, items);
            });
            it.remove();
        }
        return count[0];
    }

    private int flushQueue(MinecraftServer server, UUID owner, List<QueuedItem> items)
    {
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        if(player == null)
            return 0;
        int[] count = {0};
        for(QueuedItem item : items)
        {
            Vec3 pos = player.position();
            ItemEntity entity = new ItemEntity(player.level(), pos.x, pos.y, pos.z, item.stack.copyAndClear());
            entity.setDefaultPickUpDelay();
            entity.setExtendedLifetime();
            player.level().addFreshEntity(entity);
            count[0]++;
        }
        return count[0];
    }

    private void flushItem(ServerLevel level, Vec3 position, ItemStack stack, boolean unlimited)
    {
        if(stack.isEmpty())
            return;
        ItemEntity entity = new ItemEntity(this.level, position.x, position.y, position.z, stack.copyAndClear());
        entity.setDefaultPickUpDelay();
        entity.setExtendedLifetime();
        if(unlimited)
            entity.setUnlimitedLifetime();
        level.addFreshEntity(entity);
    }

    private void removeAndFlushQueueToBlockPos(UUID shelfId, BlockPos pos)
    {
        Shelf shelf = this.shelves.remove(shelfId);
        if(shelf == null)
            return;
        shelf.forEachQueue((owner, items) -> items.forEach(item -> {
            this.flushItem(this.level, pos.getCenter(), item.stack, false);
        }));
    }

    public void tick()
    {
        // Only run 4 times a second, or when
        this.timer++;
        if(!this.runNow && this.timer % 5 != 0)
            return;

        Map<UUID, Shelf> corrected = null;
        var it = this.shelves.entrySet().iterator();
        while(it.hasNext())
        {
            var entry = it.next();
            UUID id = entry.getKey();
            Shelf shelf = entry.getValue();
            assert shelf != null;

            // If the area is not loaded, backpacks can not be returned, so we'll hold off
            BlockPos blockPos = shelf.pos;
            if(!this.force && !this.level.isLoaded(blockPos))
                continue;

            // If the block entity is not a shelf, unregistered and clear any existing queue
            if(!(this.level.getBlockEntity(blockPos) instanceof ShelfBlockEntity shelfBlockEntity))
            {
                shelf.forEachQueue((owner, items) -> items.forEach(item -> {
                    this.flushItem(this.level, shelf.pos.getCenter(), item.stack, false);
                }));
                it.remove();
                this.setDirty();
                continue;
            }

            // Verify that the shelf has the same id, otherwise perform correction
            if(!id.equals(shelfBlockEntity.id()))
            {
                it.remove();
                if(corrected == null)
                    corrected = new HashMap<>();
                corrected.put(shelfBlockEntity.id(), shelf);
                this.setDirty();
                continue;
            }

            Map<UUID, List<QueuedItem>> playerToQueue = shelf.queues();
            if(playerToQueue == null)
                continue;

            // Collect all queued items for the shelf and sort by the time they were queued
            List<QueuedItem> items = playerToQueue.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .sorted(Comparator.comparing(QueuedItem::time))
                .collect(Collectors.toCollection(ArrayList::new));

            // Put the latest queued item on the shelf
            ItemStack existing = shelfBlockEntity.getBackpack();
            if(!items.isEmpty()) {
                QueuedItem last = items.removeLast();
                shelfBlockEntity.setBackpack(last.stack.copyAndClear());
            }

            // If shelf had an item, spawn into level at the shelf
            if(!existing.isEmpty()) {
                this.flushItem(this.level, shelfBlockEntity.getBlockPos().getCenter(), existing, true);
            }

            // Finally, spawn all other queued items into the level at the shelf
            for(QueuedItem item : items) {
                this.flushItem(this.level, shelfBlockEntity.getBlockPos().getCenter(), item.stack, true);
            }

            shelf.resetQueue();
            this.setDirty();
        }

        if(corrected != null)
        {
            this.shelves.putAll(corrected);
            this.setDirty();
        }

        this.runNow = false;
        this.force = false;
    }

    private static Recall load(ServerLevel level, HolderLookup.Provider provider, CompoundTag tag)
    {
        Recall recall = new Recall(level);
        recall.timer = tag.getInt("timer");
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag shelvesList = tag.getList("Shelves", Tag.TAG_COMPOUND);
        shelvesList.forEach(nbt -> {
            if(nbt instanceof CompoundTag shelfTag) {
                try {
                    UUID id = shelfTag.getUUID("Id");
                    BlockPos pos = BlockPos.of(shelfTag.getLong("Pos"));
                    if(level.isOutsideBuildHeight(pos))
                        throw new IllegalArgumentException("Shelf block position is outside the valid build height");
                    Map<UUID, List<QueuedItem>> queues = new HashMap<>();
                    ListTag queuesList = shelfTag.getList("Queues", Tag.TAG_COMPOUND);
                    queuesList.forEach(nbt1 -> {
                        if(nbt1 instanceof CompoundTag queueTag) {
                            try {
                                UUID owner = queueTag.getUUID("Owner");
                                List<QueuedItem> items = QueuedItem.CODEC.listOf()
                                    .parse(ops, queueTag.get("QueuedItems"))
                                    .resultOrPartial(Constants.LOG::error)
                                    .map(ArrayList::new).orElse(new ArrayList<>());
                                items.removeIf(item -> item.stack.isEmpty());
                                if(!items.isEmpty()) {
                                    queues.put(owner, items);
                                }
                            } catch (Exception e) {
                                Constants.LOG.error("An error occurred while reading Recall queue entry", e);
                            }
                        }
                    });
                    recall.shelves.put(id, new Shelf(pos, queues));
                } catch (Exception e) {
                    Constants.LOG.error("An error occurred while reading Recall shelf entry", e);
                }
            }
        });
        return recall;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.putInt("Timer", this.timer);
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag shelves = new ListTag();
        this.shelves.forEach((id, shelf) -> {
            CompoundTag shelfTag = new CompoundTag();
            shelfTag.putUUID("Id", id);
            shelfTag.putLong("Pos", shelf.pos.asLong());
            Map<UUID, List<QueuedItem>> queue = shelf.queues();
            if(queue != null && !queue.isEmpty()) {
                ListTag queuesList = new ListTag();
                queue.forEach((owner, items) -> {
                    CompoundTag queueTag = new CompoundTag();
                    queueTag.putUUID("Owner", owner);
                    QueuedItem.CODEC.listOf()
                        .encodeStart(ops, items)
                        .resultOrPartial(Constants.LOG::error)
                        .ifPresent(t -> queueTag.put("QueuedItems", t));
                    queuesList.add(queueTag);
                });
                shelfTag.put("Queues", queuesList);
            }
            shelves.add(shelfTag);
        });
        tag.put("Shelves", shelves);
        return tag;
    }

    private static final class Shelf
    {
        private final BlockPos pos;
        private @Nullable Map<UUID, List<QueuedItem>> queues;

        private Shelf(BlockPos pos)
        {
            this.pos = pos;
        }

        private Shelf(BlockPos pos, @Nullable Map<UUID, List<QueuedItem>> queues)
        {
            this.pos = pos;
            this.queues = queues != null && !queues.isEmpty() ? queues : null;
        }

        @Nullable
        public Map<UUID, List<QueuedItem>> queues()
        {
            return this.queues;
        }

        public void forEachQueue(BiConsumer<UUID, List<QueuedItem>> consumer)
        {
            if(this.queues != null)
            {
                this.queues.forEach(consumer);
            }
        }

        public boolean queues(ServerPlayer player, ItemStack backpack, int time)
        {
            if(this.queues == null)
                this.queues = new HashMap<>();
            List<QueuedItem> items = this.queues.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
            if(items.size() >= MAX_QUEUE_SIZE)
                return false;
            items.add(new QueuedItem(backpack.copyAndClear(), time));
            return true;
        }

        public void resetQueue()
        {
            this.queues = null;
        }
    }

    private record QueuedItem(ItemStack stack, int time)
    {
        private static final Codec<QueuedItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("item").orElse(ItemStack.EMPTY).forGetter(QueuedItem::stack),
            Codec.INT.fieldOf("queued_at").orElse(0).forGetter(QueuedItem::time)
        ).apply(instance, QueuedItem::new));
    }

    public interface Access
    {
        Recall backpacked$getRecall();
    }
}
