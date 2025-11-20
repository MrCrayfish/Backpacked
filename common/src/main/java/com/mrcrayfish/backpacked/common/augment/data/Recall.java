package com.mrcrayfish.backpacked.common.augment.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import java.util.stream.Collectors;

public final class Recall extends SavedData
{
    public static final String ID = "backpacked_recall";
    private static final int MAX_QUEUE_SIZE = 16;

    private final ServerLevel level;
    private final BiMap<UUID, BlockPos> shelves = HashBiMap.create();
    private final Map<UUID, Map<UUID, List<QueuedItem>>> queues = new HashMap<>();
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
            this.shelves.forcePut(shelf.id(), shelf.getBlockPos());
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
    public BlockPos getShelfBlockPos(UUID id)
    {
        return this.shelves.get(id);
    }

    public boolean recallToShelf(ServerPlayer player, UUID shelfId, ItemStack backpack)
    {
        if(this.shelves.containsKey(shelfId))
        {
            Map<UUID, List<QueuedItem>> playerToQueue = this.queues.computeIfAbsent(shelfId, k -> new HashMap<>());
            List<QueuedItem> items = playerToQueue.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
            if(items.size() >= MAX_QUEUE_SIZE)
                return false;
            items.add(new QueuedItem(backpack.copyAndClear(), this.timer));
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
        var it = this.queues.entrySet().iterator();
        while(it.hasNext())
        {
            Map<UUID, List<QueuedItem>> playerToQueue = it.next().getValue();
            playerToQueue.forEach((owner, items) -> {
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
        Map<UUID, List<QueuedItem>> playerToQueue = this.queues.remove(shelfId);
        if(playerToQueue == null)
            return;
        playerToQueue.values().forEach(items -> items.forEach(item -> {
            this.flushItem(this.level, pos.getCenter(), item.stack, false);
        }));
    }

    public void tick()
    {
        // Only run 4 times a second, or when
        this.timer++;
        if(!this.runNow && this.timer % 5 != 0)
            return;

        var it = this.shelves.entrySet().iterator();
        while(it.hasNext())
        {
            var entry = it.next();
            BlockPos blockPos = entry.getValue();
            assert blockPos != null; // BiMap doesn't allow null so this should be fine

            // If the area is not loaded, backpacks can not be returned, so we'll hold off
            if(!this.force && !this.level.isLoaded(blockPos))
                continue;

            // If the block entity is not a shelf, unregistered and clear any existing queue
            if(!(this.level.getBlockEntity(blockPos) instanceof ShelfBlockEntity shelf))
            {
                it.remove();
                this.queues.remove(entry.getKey());
                // TODO determine what to do with undeliverable backpacks
                this.setDirty();
                continue;
            }

            // Ensure the shelf is registered
            this.onShelfLoaded(shelf);

            Map<UUID, List<QueuedItem>> playerToQueue = this.queues.remove(shelf.id());
            if(playerToQueue == null)
                continue;

            // Collect all queued items for the shelf and sort by the time they were queued
            List<QueuedItem> items = playerToQueue.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .sorted(Comparator.comparing(QueuedItem::time))
                .collect(Collectors.toCollection(ArrayList::new));

            // Put the latest queued item on the shelf
            ItemStack existing = shelf.getBackpack();
            if(!items.isEmpty()) {
                QueuedItem last = items.removeLast();
                shelf.setBackpack(last.stack.copyAndClear());
            }

            // If shelf had an item, spawn into level at the shelf
            if(!existing.isEmpty()) {
                this.flushItem(this.level, shelf.getBlockPos().getCenter(), existing, true);
            }

            // Finally, spawn all other queued items into the level at the shelf
            for(QueuedItem item : items) {
                this.flushItem(this.level, shelf.getBlockPos().getCenter(), item.stack, true);
            }

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
                    UUID id = shelfTag.getUUID("ShelfId");
                    BlockPos pos = BlockPos.of(shelfTag.getLong("ShelfPos"));
                    if(level.isOutsideBuildHeight(pos))
                        throw new IllegalArgumentException("Shelf block position is outside the valid build height");
                    recall.shelves.forcePut(id, pos);
                    ListTag playerQueuesList = shelfTag.getList("PlayerQueues", Tag.TAG_COMPOUND);
                    playerQueuesList.forEach(nbt1 -> {
                        if(nbt1 instanceof CompoundTag queueTag) {
                            try {
                                UUID owner = queueTag.getUUID("Owner");
                                List<QueuedItem> items = QueuedItem.CODEC.listOf()
                                    .parse(ops, queueTag.get("QueuedItems"))
                                    .resultOrPartial(Constants.LOG::error)
                                    .map(ArrayList::new).orElse(new ArrayList<>());
                                items.removeIf(item -> item.stack.isEmpty());
                                if(!items.isEmpty()) {
                                    Map<UUID, List<QueuedItem>> playerToQueue = recall.queues.computeIfAbsent(id, k -> new HashMap<>());
                                    playerToQueue.computeIfAbsent(owner, k -> new ArrayList<>()).addAll(items);
                                }
                            } catch (Exception e) {
                                Constants.LOG.error("An error occurred while reading Recall queue entry", e);
                            }
                        }
                    });

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
        this.shelves.forEach((id, pos) -> {
            if(id == null || pos == null)
                return;
            CompoundTag shelfTag = new CompoundTag();
            shelfTag.putUUID("ShelfId", id);
            shelfTag.putLong("ShelfPos", pos.asLong());
            Map<UUID, List<QueuedItem>> playerQueues = this.queues.get(id);
            if(playerQueues != null && !playerQueues.isEmpty()) {
                ListTag playerQueuesList = new ListTag();
                playerQueues.forEach((owner, items) -> {
                    CompoundTag queueTag = new CompoundTag();
                    queueTag.putUUID("Owner", owner);
                    QueuedItem.CODEC.listOf()
                        .encodeStart(ops, items)
                        .resultOrPartial(Constants.LOG::error)
                        .ifPresent(t -> queueTag.put("QueuedItems", t));
                    playerQueuesList.add(queueTag);
                });
                shelfTag.put("PlayerQueues", playerQueuesList);
            }
            shelves.add(shelfTag);
        });
        tag.put("Shelves", shelves);
        return tag;
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
