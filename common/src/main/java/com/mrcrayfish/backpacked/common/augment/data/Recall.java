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
    private static final int MAX_QUEUE_SIZE = 64;

    private final ServerLevel level;
    private final BiMap<UUID, BlockPos> shelves = HashBiMap.create();
    private final Map<UUID, List<OwnedItem>> queue = new HashMap<>();
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
            List<OwnedItem> list = this.queue.computeIfAbsent(shelfId, k -> new ArrayList<>());
            if(list.size() >= MAX_QUEUE_SIZE)
                return false;
            list.add(new OwnedItem(player.getUUID(), backpack.copyAndClear()));
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
        var it = this.queue.entrySet().iterator();
        while(it.hasNext())
        {
            List<OwnedItem> ownedItems = it.next().getValue();
            count[0] += this.flushQueue(server, ownedItems);
            it.remove();
        }
        return count[0];
    }

    private int flushQueue(MinecraftServer server, List<OwnedItem> ownedItems)
    {
        int[] count = {0};
        var it = ownedItems.iterator();
        while(it.hasNext()) {
            OwnedItem backpack = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(backpack.owner);
            if(player == null)
                continue;
            Vec3 pos = player.position();
            ItemEntity entity = new ItemEntity(player.level(), pos.x, pos.y, pos.z, backpack.stack);
            entity.setDefaultPickUpDelay();
            entity.setExtendedLifetime();
            player.level().addFreshEntity(entity);
            it.remove();
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
        List<OwnedItem> ownedItems = this.queue.remove(shelfId);
        if(ownedItems != null)
        {
            for(OwnedItem ownedItem : ownedItems)
            {
                this.flushItem(this.level, pos.getCenter(), ownedItem.stack, false);
            }
        }
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
                this.queue.remove(entry.getKey());
                // TODO determine what to do with undeliverable backpacks
                this.setDirty();
                continue;
            }

            // Ensure the shelf is registered
            this.onShelfLoaded(shelf);

            List<OwnedItem> items = this.queue.get(shelf.id());
            if(items == null)
                continue;

            if(!items.isEmpty())
            {
                ItemStack existing = shelf.getBackpack();
                OwnedItem last = items.removeLast();
                shelf.setBackpack(last.stack.copyAndClear());
                if(!existing.isEmpty())
                {
                    this.flushItem(this.level, shelf.getBlockPos().getCenter(), existing.copyAndClear(), true);
                }
                for(OwnedItem item : items)
                {
                    this.flushItem(this.level, shelf.getBlockPos().getCenter(), item.stack.copyAndClear(), true);
                }
            }

            this.queue.remove(shelf.id());
            this.setDirty();
        }

        this.runNow = false;
        this.force = false;
    }

    private static Recall load(ServerLevel level, HolderLookup.Provider provider, CompoundTag tag)
    {
        Recall recall = new Recall(level);
        ListTag shelvesList = tag.getList("Shelves", Tag.TAG_COMPOUND);
        shelvesList.forEach(nbt -> {
            if(nbt instanceof CompoundTag entry) {
                try {
                    UUID id = entry.getUUID("ShelfId");
                    BlockPos pos = BlockPos.of(entry.getLong("ShelfPos"));
                    if(level.isOutsideBuildHeight(pos))
                        throw new IllegalArgumentException("Shelf block position is outside the valid build height");
                    recall.shelves.forcePut(id, pos);
                } catch (Exception e) {
                    Constants.LOG.error("An error occurred while reading Recall shelf entry", e);
                }
            }
        });

        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag queueList = tag.getList("Queue", Tag.TAG_COMPOUND);
        queueList.forEach(nbt -> {
            if(nbt instanceof CompoundTag entryTag) {
                UUID id;
                try {
                     id = entryTag.getUUID("ShelfId");
                } catch(Exception e) {
                    Constants.LOG.error("Missing ShelfId when reading Recall queue", e);
                    return;
                }
                List<OwnedItem> list = new ArrayList<>();
                ListTag ownedItemsList = entryTag.getList("OwnedItems", Tag.TAG_COMPOUND);
                ownedItemsList.forEach(nbt1 -> {
                    if(nbt1 instanceof CompoundTag ownedItemTag) {
                        try {
                            UUID owner = ownedItemTag.getUUID("Owner");
                            ItemStack stack = ItemStack.OPTIONAL_CODEC.parse(ops, ownedItemTag.get("Item"))
                                .resultOrPartial(Constants.LOG::error)
                                .orElse(ItemStack.EMPTY);
                            if(!stack.isEmpty()) {
                                list.add(new OwnedItem(owner, stack));
                            } else {
                                Constants.LOG.warn("Skipping Recall queue entry since the stack is empty");
                            }
                        } catch(Exception e) {
                            Constants.LOG.error("An error occurred while reading Recall queue entry", e);
                        }
                    }
                });
                recall.queue.put(id, list);
            }
        });

        return recall;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
    {
        ListTag shelves = new ListTag();
        this.shelves.forEach((id, pos) -> {
            if(id == null || pos == null)
                return;
            CompoundTag entry = new CompoundTag();
            entry.putUUID("ShelfId", id);
            entry.putLong("ShelfPos", pos.asLong());
            shelves.add(entry);
        });
        tag.put("Shelves", shelves);

        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag queueList = new ListTag();
        this.queue.forEach((id, backpacks) -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("ShelfId", id);
            ListTag ownedItemsList = new ListTag();
            backpacks.forEach(backpack -> {
                CompoundTag ownedItemTag = new CompoundTag();
                ownedItemTag.putUUID("Owner", backpack.owner);
                ItemStack.OPTIONAL_CODEC.encodeStart(ops, backpack.stack)
                    .resultOrPartial(Constants.LOG::error)
                    .ifPresent(t -> ownedItemTag.put("Item", t));
                ownedItemsList.add(ownedItemTag);
            });
            entryTag.put("OwnedItems", ownedItemsList);
            queueList.add(entryTag);
        });
        tag.put("Queue", queueList);

        return tag;
    }

    private record OwnedItem(UUID owner, ItemStack stack) {}

    public interface Access
    {
        Recall backpacked$getRecall();
    }
}
