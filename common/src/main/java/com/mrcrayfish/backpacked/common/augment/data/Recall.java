package com.mrcrayfish.backpacked.common.augment.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.BackpackedCodecs;
import com.mrcrayfish.backpacked.common.ShelfKey;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModPointOfInterests;
import com.mrcrayfish.backpacked.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public final class Recall extends SavedData
{
    public static final Identifier ID = Utils.id("recall");
    public static final int MAX_QUEUE_SIZE = 18;
    public static final Codec<Recall> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(
            BackpackedCodecs.SECTION_POS,
            Codec.unboundedMap(Codec.SHORT, ShelfQueue.CODEC).xmap(Short2ObjectOpenHashMap::new, Short2ObjectOpenHashMap::new)
        ).xmap(Object2ObjectOpenHashMap::new, Object2ObjectOpenHashMap::new).fieldOf("Tasks").forGetter(f -> f.queues),
        Codec.INT.optionalFieldOf("Timer", 0).forGetter(f -> f.timer)
    ).apply(instance, Recall::new));
    public static final SavedDataType<Recall> TYPE = new SavedDataType<>(ID, () -> new Recall(new Object2ObjectOpenHashMap<>(), 0), CODEC, null);

    private final Object2ObjectOpenHashMap<SectionPos, Short2ObjectOpenHashMap<ShelfQueue>> queues;
    private int timer;
    private boolean runNow;
    private boolean force;

    private Recall(Object2ObjectOpenHashMap<SectionPos, Short2ObjectOpenHashMap<ShelfQueue>> queues, int timer)
    {
        this.queues = queues;
        this.timer = timer;
    }

    public void onShelfBroken(ServerLevel level, ShelfBlockEntity shelf)
    {
        this.removeAndFlushQueueToBlockPos(level, shelf.key());
    }

    public boolean recallToShelf(ServerLevel level, ServerPlayer player, ShelfKey key, int originalIndex, ItemStack backpack)
    {
        BlockPos pos = BlockPos.of(key.position());
        if(!level.isInWorldBounds(pos))
            return false;

        if(!this.isShelfAtBlockPos(level, pos))
            return false;

        ShelfQueue queue = this.getOrCreateShelfQueue(pos);
        if(!queue.add(player, originalIndex, backpack, this.timer))
            return false;

        this.runNow = true;
        this.setDirty();
        return true;
    }

    private ShelfQueue getOrCreateShelfQueue(BlockPos pos)
    {
        SectionPos sectionPos = SectionPos.of(pos);
        short relativePos = SectionPos.sectionRelativePos(pos);
        var sectionMap = this.queues.computeIfAbsent(sectionPos, k -> new Short2ObjectOpenHashMap<>());
        return sectionMap.computeIfAbsent(relativePos, k -> new ShelfQueue());
    }

    public boolean isShelfAtBlockPos(ServerLevel level, BlockPos pos)
    {
        if(!level.isInWorldBounds(pos))
            return false;

        if(level.getPoiManager().existsAtPosition(ModPointOfInterests.BACKPACK_SHELF.key(), pos))
            return true;

        if(!level.isLoaded(pos))
            return false;

        BlockState state = level.getBlockState(pos);
        if(!(state.getBlock() instanceof ShelfBlock))
            return false;

        var optional = PoiTypes.forState(state);
        if(optional.isEmpty())
            return false;

        level.getPoiManager().add(pos, optional.get());
        return true;
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
            it.next().getValue().forEach((relativePos, shelfQueue) -> {
                shelfQueue.forEach((owner, items) -> {
                    count[0] += this.flushQueue(server, owner, items);
                });
            });
            it.remove();
            this.setDirty();
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

    private void flushItem(ServerLevel level, Vec3 position, ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            ItemEntity entity = new ItemEntity(level, position.x, position.y, position.z, stack.copyAndClear());
            entity.setDefaultPickUpDelay();
            entity.setExtendedLifetime();
            level.addFreshEntity(entity);
        }
    }

    private void removeAndFlushQueueToBlockPos(ServerLevel level, ShelfKey key)
    {
        BlockPos pos = BlockPos.of(key.position());
        SectionPos sectionPos = SectionPos.of(pos);
        var sectionMap = this.queues.get(sectionPos);
        if(sectionMap != null)
        {
            short relativePos = SectionPos.sectionRelativePos(pos);
            ShelfQueue queue = sectionMap.remove(relativePos);
            if(queue != null)
            {
                // Flush items of every player queue
                queue.forEach((owner, items) -> items.forEach(item -> {
                    this.removeInvalidShelfFromItemStack(item.stack);
                    this.flushItem(level, pos.getCenter(), item.stack);
                }));

                // Clean up
                if(sectionMap.isEmpty())
                {
                    this.queues.remove(sectionPos);
                }

                // Since we have removed things, mark as needing to be saved
                this.setDirty();
            }
        }
    }

    private void removeInvalidShelfFromItemStack(ItemStack stack)
    {
        RecallAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.RECALL.get());
        if(augment != null)
        {
            augment = augment.setShelfKey(null);
            Augments augments = Augments.get(stack);
            for(Augments.Position position : Augments.Position.values())
            {
                if(augments.getAugment(position).type() == ModAugmentTypes.RECALL.get())
                {
                    augments.setAugment(position, augment);
                    break;
                }
            }
            Augments.set(stack, augments);
        }
    }

    public void tick(ServerLevel level)
    {
        // Only run 4 times a second, or when
        this.timer++;
        if(!this.runNow && this.timer % 5 != 0)
            return;

        // Don't perform tick if level contains no players
        if(level.players().isEmpty())
            return;

        var sectionIterator = this.queues.entrySet().iterator();
        while(sectionIterator.hasNext())
        {
            var sectionEntry = sectionIterator.next();
            var relativeIterator = sectionEntry.getValue().short2ObjectEntrySet().iterator();
            while(relativeIterator.hasNext())
            {
                var relativeEntry = relativeIterator.next();
                BlockPos pos = sectionEntry.getKey().relativeToBlockPos(relativeEntry.getShortKey());
                if(!this.force && !level.isLoaded(pos))
                    continue;

                var shelfOptional = level.getBlockEntity(pos, ModBlockEntities.BACKPACK_SHELF.get());
                if(shelfOptional.isEmpty() || !this.isShelfAtBlockPos(level, pos))
                {
                    ShelfQueue queue = relativeEntry.getValue();
                    queue.forEach((owner, items) -> items.forEach(item -> {
                        this.removeInvalidShelfFromItemStack(item.stack);
                        this.flushItem(level, pos.getCenter(), item.stack);
                    }));
                    sectionIterator.remove();
                    this.setDirty();
                    continue;
                }

                ShelfQueue queue = relativeEntry.getValue();
                ShelfBlockEntity shelf = shelfOptional.get();
                Map<UUID, List<QueuedItem>> playerToQueue = queue.queues();
                if(playerToQueue != null)
                {
                    if(shelf.getBackpack().isEmpty())
                    {
                        // Find the list that contains a queued item that has been waiting
                        // the longest to recall, which is the first item in each list.
                        var playerQueue = getMinimumQueue(playerToQueue);
                        if(playerQueue != null)
                        {
                            QueuedItem item = playerQueue.getSecond().removeFirst();
                            shelf.recall(item.stack, playerQueue.getFirst(), item.originalIndex);
                            queue.decrementCount();
                            queue.cleanQueues();
                            this.setDirty();
                        }
                    }
                }

                shelf.setRecallQueueCount(queue.count);

                // Clean up
                if(queue.isEmpty())
                {
                    relativeIterator.remove();
                    this.setDirty();
                }
            }

            // Clean up
            if(sectionEntry.getValue().isEmpty())
            {
                sectionIterator.remove();
                this.setDirty();
            }
        }

        this.runNow = false;
        this.force = false;
    }

    @Nullable
    private static Pair<UUID, List<QueuedItem>> getMinimumQueue(Map<UUID, List<QueuedItem>> playerToQueue)
    {
        UUID owner = null;
        List<QueuedItem> minItems = null;
        for(var entry : playerToQueue.entrySet())
        {
            var items = entry.getValue();
            if(items.isEmpty())
                continue;

            // Just a simple min comparison, lower time = older
            if(minItems == null || items.getFirst().time < minItems.getFirst().time)
            {
                owner = entry.getKey();
                minItems = items;
            }
        }
        return minItems != null ? Pair.of(owner, minItems) : null;
    }

    private static final class ShelfQueue
    {
        private static final Codec<ShelfQueue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.CODEC, QueuedItem.CODEC.listOf()).optionalFieldOf("Queues", new HashMap<>()).forGetter(s -> s.queues)
        ).apply(instance, ShelfQueue::new));

        private Map<UUID, List<QueuedItem>> queues;
        private int count;

        private ShelfQueue()
        {
            this(new HashMap<>());
        }

        private ShelfQueue(Map<UUID, List<QueuedItem>> queues)
        {
            this.queues = queues;
            this.cleanQueues();
            this.updateCount();
        }

        @Nullable
        public Map<UUID, List<QueuedItem>> queues()
        {
            return this.queues;
        }

        public void forEach(BiConsumer<UUID, List<QueuedItem>> consumer)
        {
            if(this.queues != null)
            {
                this.queues.forEach(consumer);
            }
        }

        public boolean add(ServerPlayer player, int originalIndex, ItemStack backpack, int time)
        {
            if(this.queues == null)
            {
                this.count = 0;
                this.queues = new HashMap<>();
            }
            List<QueuedItem> items = this.queues.computeIfAbsent(player.getUUID(), k -> new LinkedList<>());
            if(items.size() >= MAX_QUEUE_SIZE)
                return false;
            items.add(new QueuedItem(originalIndex, backpack.copyAndClear(), time));
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
                    this.count = 0;
                }
            }
        }

        private boolean isEmpty()
        {
            return this.queues == null || this.queues.isEmpty();
        }
    }

    private record QueuedItem(int originalIndex, ItemStack stack, int time)
    {
        private static final Codec<QueuedItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("original_index").orElse(-1).forGetter(QueuedItem::originalIndex),
            ItemStack.OPTIONAL_CODEC.fieldOf("item").orElse(ItemStack.EMPTY).forGetter(QueuedItem::stack),
            Codec.INT.fieldOf("queued_at").orElse(0).forGetter(QueuedItem::time)
        ).apply(instance, QueuedItem::new));
    }

    public interface Access
    {
        Recall backpacked$getRecall();
    }
}
