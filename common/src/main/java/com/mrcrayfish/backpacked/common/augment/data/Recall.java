package com.mrcrayfish.backpacked.common.augment.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.ShelfKey;
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

public final class Recall extends SavedData
{
    public static final String ID = "backpacked_recall";
    private static final int MAX_QUEUE_SIZE = 16;

    private final ServerLevel level;
    private final Map<Long, Shelf> shelves = new HashMap<>();
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
        long position = shelf.key().position();
        if(!this.shelves.containsKey(position))
        {
            this.shelves.put(position, new Shelf(shelf.getBlockPos()));
            this.setDirty();
        }
    }

    public void onShelfBroken(ShelfBlockEntity shelf)
    {
        long position = shelf.key().position();
        this.shelves.remove(position);
        this.removeAndFlushQueueToBlockPos(shelf.key());
        this.setDirty();
    }

    public boolean isShelfKnown(BlockPos pos)
    {
        return this.shelves.containsKey(pos.asLong());
    }

    public boolean recallToShelf(ServerPlayer player, ShelfKey key, ItemStack backpack)
    {
        Shelf shelf = this.shelves.get(key.position());
        if(shelf != null)
        {
            if(!shelf.queue(player, backpack, this.timer))
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

    private void removeAndFlushQueueToBlockPos(ShelfKey key)
    {
        Shelf shelf = this.shelves.remove(key.position());
        if(shelf == null)
            return;
        BlockPos pos = BlockPos.of(key.position());
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

        var it = this.shelves.entrySet().iterator();
        while(it.hasNext())
        {
            var entry = it.next();
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

            Map<UUID, List<QueuedItem>> playerToQueue = shelf.queues();
            if(playerToQueue != null)
            {
                if(shelfBlockEntity.getBackpack().isEmpty())
                {
                    // Find the list that contains a queued item that has been waiting
                    // the longest to recall, which is the first item in each list.
                    List<QueuedItem> minItems = getMinimumQueue(playerToQueue);
                    if(minItems != null)
                    {
                        QueuedItem item = minItems.removeFirst();
                        shelfBlockEntity.setBackpack(item.stack.copyAndClear());
                        shelf.decrementCount();
                        shelf.cleanQueues();
                        this.setDirty();
                    }
                }
            }
            shelfBlockEntity.setRecallQueueCount(shelf.count);
        }

        this.runNow = false;
        this.force = false;
    }

    @Nullable
    private static List<QueuedItem> getMinimumQueue(Map<UUID, List<QueuedItem>> playerToQueue)
    {
        List<QueuedItem> minItems = null;
        for(List<QueuedItem> items : playerToQueue.values())
        {
            if(items.isEmpty())
                continue;

            // Just a simple min comparison, lower time = older
            if(minItems == null || items.getFirst().time < minItems.getFirst().time)
            {
                minItems = items;
            }
        }
        return minItems;
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
                    if(!shelfTag.contains("Pos", Tag.TAG_LONG))
                        return;
                    long posAsLong = shelfTag.getLong("Pos");
                    BlockPos pos = BlockPos.of(posAsLong);
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
                                    .map(LinkedList::new).orElse(new LinkedList<>());
                                items.removeIf(item -> item.stack.isEmpty());
                                if(!items.isEmpty()) {
                                    queues.put(owner, items);
                                }
                            } catch (Exception e) {
                                Constants.LOG.error("An error occurred while reading Recall queue entry", e);
                            }
                        }
                    });
                    recall.shelves.put(posAsLong, new Shelf(pos, queues));
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
        this.shelves.forEach((pos, shelf) -> {
            CompoundTag shelfTag = new CompoundTag();
            shelfTag.putLong("Pos", pos);
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
        private int count;

        private Shelf(BlockPos pos)
        {
            this.pos = pos;
        }

        private Shelf(BlockPos pos, @Nullable Map<UUID, List<QueuedItem>> queues)
        {
            this.pos = pos;
            this.queues = queues != null && !queues.isEmpty() ? queues : null;
            this.cleanQueues();
            this.updateCount();
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

        public boolean queue(ServerPlayer player, ItemStack backpack, int time)
        {
            if(this.queues == null)
            {
                this.count = 0;
                this.queues = new HashMap<>();
            }
            List<QueuedItem> items = this.queues.computeIfAbsent(player.getUUID(), k -> new LinkedList<>());
            if(items.size() >= MAX_QUEUE_SIZE)
                return false;
            items.add(new QueuedItem(backpack.copyAndClear(), time));
            this.count++;
            return true;
        }

        private void decrementCount()
        {
            if(this.count > 0)
            {
                this.count--;
            }
        }

        private void updateCount()
        {
            if(this.queues != null)
            {
                this.count = 0;
                for(List<QueuedItem> items : this.queues.values())
                {
                    this.count += items.size();
                }
            }
        }

        private void cleanQueues()
        {
            if(this.queues != null)
            {
                this.queues.entrySet().removeIf(e -> e.getValue().isEmpty());
                if(this.queues.isEmpty())
                {
                    this.queues = null;
                    this.count = 0;
                }
            }
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
