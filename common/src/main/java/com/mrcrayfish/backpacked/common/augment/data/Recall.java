package com.mrcrayfish.backpacked.common.augment.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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

public final class Recall extends SavedData
{
    public static final String ID = "backpacked_recall";
    private static final int MAX_QUEUE_SIZE = 16;

    private final ServerLevel level;
    private final BiMap<UUID, BlockPos> shelves = HashBiMap.create();
    private final Map<UUID, Map<UUID, List<ItemStack>>> queues = new HashMap<>();
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
            Map<UUID, List<ItemStack>> playerToQueue = this.queues.computeIfAbsent(shelfId, k -> new HashMap<>());
            List<ItemStack> items = playerToQueue.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
            if(items.size() >= MAX_QUEUE_SIZE)
                return false;
            items.add(backpack.copyAndClear());
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
            Map<UUID, List<ItemStack>> playerToQueue = it.next().getValue();
            playerToQueue.forEach((owner, items) -> {
                count[0] += this.flushQueue(server, owner, items);
            });
            it.remove();
        }
        return count[0];
    }

    private int flushQueue(MinecraftServer server, UUID owner, List<ItemStack> items)
    {
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        if(player == null)
            return 0;
        int[] count = {0};
        for(ItemStack stack : items)
        {
            Vec3 pos = player.position();
            ItemEntity entity = new ItemEntity(player.level(), pos.x, pos.y, pos.z, stack.copyAndClear());
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
        Map<UUID, List<ItemStack>> playerToQueue = this.queues.remove(shelfId);
        if(playerToQueue == null)
            return;

        playerToQueue.values().forEach(items -> {
            items.forEach(stack -> {
                this.flushItem(this.level, pos.getCenter(), stack, false);
            });
        });
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

            Map<UUID, List<ItemStack>> playerToQueue = this.queues.remove(shelf.id());
            if(playerToQueue == null)
                continue;
            playerToQueue.forEach((owner, items) -> {
                ItemStack existing = shelf.getBackpack();
                ItemStack last = items.removeLast();
                shelf.setBackpack(last.copyAndClear());
                if(!existing.isEmpty()) {
                    this.flushItem(this.level, shelf.getBlockPos().getCenter(), existing, true);
                }
                for(ItemStack stack : items) {
                    this.flushItem(this.level, shelf.getBlockPos().getCenter(), stack, true);
                }
            });
            this.setDirty();
        }

        this.runNow = false;
        this.force = false;
    }

    private static Recall load(ServerLevel level, HolderLookup.Provider provider, CompoundTag tag)
    {
        Recall recall = new Recall(level);
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
                                List<ItemStack> items = ItemStack.OPTIONAL_CODEC.listOf()
                                    .parse(ops, queueTag.get("Items"))
                                    .resultOrPartial(Constants.LOG::error)
                                    .map(ArrayList::new).orElse(new ArrayList<>());
                                if(!items.isEmpty()) {
                                    Map<UUID, List<ItemStack>> playerToQueue = recall.queues.computeIfAbsent(id, k -> new HashMap<>());
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
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag shelves = new ListTag();
        this.shelves.forEach((id, pos) -> {
            if(id == null || pos == null)
                return;
            CompoundTag shelfTag = new CompoundTag();
            shelfTag.putUUID("ShelfId", id);
            shelfTag.putLong("ShelfPos", pos.asLong());
            Map<UUID, List<ItemStack>> playerQueues = this.queues.get(id);
            if(playerQueues != null && !playerQueues.isEmpty()) {
                ListTag playerQueuesList = new ListTag();
                playerQueues.forEach((owner, items) -> {
                    CompoundTag queueTag = new CompoundTag();
                    queueTag.putUUID("Owner", owner);
                    ItemStack.OPTIONAL_CODEC.listOf()
                        .encodeStart(ops, items)
                        .resultOrPartial(Constants.LOG::error)
                        .ifPresent(t -> queueTag.put("Items", t));
                    playerQueuesList.add(queueTag);
                });
                shelfTag.put("PlayerQueues", playerQueuesList);
            }
            shelves.add(shelfTag);
        });
        tag.put("Shelves", shelves);
        return tag;
    }

    public interface Access
    {
        Recall backpacked$getRecall();
    }
}
