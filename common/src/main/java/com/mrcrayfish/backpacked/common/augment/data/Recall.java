package com.mrcrayfish.backpacked.common.augment.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.ShelfKey;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModPointOfInterests;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public final class Recall extends SavedData
{
    public static final String ID = "backpacked_recall";
    private static final int MAX_QUEUE_SIZE = 18;

    private final ServerLevel level;
    private final Map<BlockPos, ShelfQueue> queues;
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
        this.queues = new HashMap<>();
    }

    public void onShelfBroken(ShelfBlockEntity shelf)
    {
        this.removeAndFlushQueueToBlockPos(shelf.key());
    }

    public boolean recallToShelf(ServerPlayer player, ShelfKey key, int originalIndex, ItemStack backpack)
    {
        BlockPos pos = BlockPos.of(key.position());
        if(!this.level.isInWorldBounds(pos))
            return false;

        if(!this.isShelfPointOfInterest(pos))
            return false;

        ShelfQueue queue = this.queues.computeIfAbsent(pos, k -> new ShelfQueue());
        if(!queue.add(player, originalIndex, backpack, this.timer))
            return false;

        this.runNow = true;
        this.setDirty();
        return true;
    }

    public boolean isShelfPointOfInterest(BlockPos pos)
    {
        if(!this.level.isInWorldBounds(pos))
            return false;

        if(this.level.getPoiManager().existsAtPosition(ModPointOfInterests.BACKPACK_SHELF.key(), pos))
            return true;

        if(!this.level.isLoaded(pos))
            return false;

        BlockState state = this.level.getBlockState(pos);
        if(!(state.getBlock() instanceof ShelfBlock))
            return false;

        var optional = PoiTypes.forState(state);
        if(optional.isEmpty())
            return false;

        this.level.getPoiManager().add(pos, optional.get());
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
            ShelfQueue shelf = it.next().getValue();
            shelf.forEach((owner, items) -> {
                count[0] += this.flushQueue(server, owner, items);
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
            ItemEntity entity = new ItemEntity(this.level, position.x, position.y, position.z, stack.copyAndClear());
            entity.setDefaultPickUpDelay();
            entity.setExtendedLifetime();
            level.addFreshEntity(entity);
        }
    }

    private void removeAndFlushQueueToBlockPos(ShelfKey key)
    {
        BlockPos pos = BlockPos.of(key.position());
        ShelfQueue shelf = this.queues.remove(pos);
        if(shelf == null)
            return;
        shelf.forEach((owner, items) -> items.forEach(item -> {
            this.removeInvalidShelfFromItemStack(item.stack);
            this.flushItem(this.level, pos.getCenter(), item.stack);
        }));
        this.setDirty();
    }

    private void removeInvalidShelfFromItemStack(ItemStack stack)
    {
        Augments augments = Augments.get(stack);
        RecallAugment augment = augments.findEnabledAndCast(ModAugmentTypes.RECALL.get());
        if(augment != null)
        {
            augment = augment.setShelfKey(null);
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

    public void tick()
    {
        // Only run 4 times a second, or when
        this.timer++;
        if(!this.runNow && this.timer % 5 != 0)
            return;

        // Don't perform tick if level contains no players
        if(this.level.players().isEmpty())
            return;

        var it = this.queues.entrySet().iterator();
        while(it.hasNext())
        {
            var entry = it.next();

            BlockPos pos = entry.getKey();
            if(!this.force && !this.level.isLoaded(pos))
                continue;

            var shelfOptional = this.level.getBlockEntity(pos, ModBlockEntities.SHELF.get());
            if(shelfOptional.isEmpty() || !this.isShelfPointOfInterest(pos))
            {
                entry.getValue().forEach((owner, items) -> items.forEach(item -> {
                    this.removeInvalidShelfFromItemStack(item.stack);
                    this.flushItem(this.level, pos.getCenter(), item.stack);
                }));
                it.remove();
                this.setDirty();
                continue;
            }

            ShelfQueue queue = entry.getValue();
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
                        shelf.setBackpack(item.stack.copyAndClear());
                        shelf.setRecallOwner(playerQueue.getFirst());
                        shelf.setRecallIndex(item.originalIndex);
                        queue.decrementCount();
                        queue.cleanQueues();
                        this.setDirty();
                    }
                }
            }

            shelf.setRecallQueueCount(queue.count);

            if(queue.isEmpty())
            {
                it.remove();
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

    private static Recall load(ServerLevel level, HolderLookup.Provider provider, CompoundTag tag)
    {
        Recall recall = new Recall(level);
        recall.timer = tag.getInt("Timer");
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag shelvesList = tag.getList("Shelves", Tag.TAG_COMPOUND);
        shelvesList.forEach(nbt -> {
            if(nbt instanceof CompoundTag shelfTag) {
                try {
                    var posOptional = NbtUtils.readBlockPos(shelfTag, "Pos");
                    if(posOptional.isEmpty())
                        throw new IllegalArgumentException("Missing shelf block position when loading queue");
                    BlockPos pos = posOptional.get();
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
                                Constants.LOG.error("An error occurred while reading Recall queues", e);
                            }
                        }
                    });
                    recall.queues.put(pos, new ShelfQueue(queues));
                } catch (Exception e) {
                    Constants.LOG.error("An error occurred while reading Recall data", e);
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
        this.queues.forEach((pos, shelf) -> {
            CompoundTag shelfTag = new CompoundTag();
            shelfTag.put("Pos", NbtUtils.writeBlockPos(pos));
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

    private static final class ShelfQueue
    {
        private @Nullable Map<UUID, List<QueuedItem>> queues;
        private int count;

        private ShelfQueue() {}

        private ShelfQueue(@Nullable Map<UUID, List<QueuedItem>> queues)
        {
            this.queues = queues != null && !queues.isEmpty() ? queues : null;
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
                    this.queues = null;
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
