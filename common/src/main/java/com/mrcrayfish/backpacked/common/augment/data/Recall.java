package com.mrcrayfish.backpacked.common.augment.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Recall extends SavedData
{
    public static final String ID = "backpacked_recall";

    private final ServerLevel level;
    private final BiMap<UUID, BlockPos> shelves = HashBiMap.create();
    private final Map<UUID, List<ItemStack>> queue = new HashMap<>();
    private int timer;
    private boolean runNow;

    @SuppressWarnings("DataFlowIssue")
    public static Factory<Recall> factory(ServerLevel level)
    {
        return new Factory<>(() -> new Recall(level), (tag, provider) -> load(level, provider, tag), null);
    }

    public Recall(ServerLevel level)
    {
        this.level = level;
    }

    public void registerShelf(ShelfBlockEntity shelf)
    {
        if(!this.shelves.containsKey(shelf.id()) || !this.shelves.containsValue(shelf.getBlockPos()))
        {
            this.shelves.forcePut(shelf.id(), shelf.getBlockPos());
            this.setDirty();
        }
    }

    public void unregisterShelf(ShelfBlockEntity shelf)
    {
        this.shelves.remove(shelf.id());
        this.shelves.inverse().remove(shelf.getBlockPos());
        this.setDirty();
    }

    @Nullable
    public BlockPos getShelfBlockPos(UUID id)
    {
        return this.shelves.get(id);
    }

    public boolean sendToShelf(UUID shelfId, ItemStack backpack)
    {
        if(this.shelves.containsKey(shelfId))
        {
            this.queue.computeIfAbsent(shelfId, k -> new ArrayList<>()).add(backpack.copyAndClear());
            this.runNow = true;
            this.setDirty();
            return true;
        }
        return false;
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
            if(!this.level.isLoaded(blockPos))
                continue;

            // If the block entity is not a shelf, unregistered and clear any existing queue
            if(!(this.level.getBlockEntity(blockPos) instanceof ShelfBlockEntity shelf))
            {
                it.remove();
                this.queue.remove(entry.getKey());
                this.setDirty();
                continue;
            }

            // Ensure the shelf is registered
            this.registerShelf(shelf);

            List<ItemStack> backpacks = this.queue.get(shelf.id());
            if(backpacks == null)
                continue;

            for(ItemStack backpack : backpacks)
            {
                if(shelf.getBackpack().isEmpty())
                {
                    // Place backpack on the shelf if it was empty
                    shelf.setBackpack(backpack.copyAndClear());
                }
                else
                {
                    // Otherwise spawn the backpack into the world at the shelf position
                    Vec3 center = blockPos.getCenter();
                    ItemEntity entity = new ItemEntity(this.level, center.x, center.y, center.z, backpack.copyAndClear());
                    entity.setUnlimitedLifetime(); // Ensure backpack doesn't despawn
                    entity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(entity);
                }
                this.setDirty();
            }

            // Finally remove the queue since it is now empty
            this.queue.remove(shelf.id());
        }

        this.runNow = false;
    }

    private static Recall load(ServerLevel level, HolderLookup.Provider provider, CompoundTag tag)
    {
        Recall recall = new Recall(level);
        ListTag shelvesList = tag.getList("Shelves", Tag.TAG_COMPOUND);
        shelvesList.forEach(nbt -> {
            if(nbt instanceof CompoundTag entry) {
                try {
                    UUID id = entry.getUUID("Id");
                    BlockPos pos = BlockPos.of(entry.getLong("Pos"));
                    if(level.isOutsideBuildHeight(pos))
                        return;
                    recall.shelves.forcePut(id, pos);
                } catch (Exception e) {
                    Constants.LOG.error("Failed to load backpack shelf", e);
                }
            }
        });

        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag queueList = tag.getList("Queue", Tag.TAG_COMPOUND);
        queueList.forEach(nbt -> {
            if(nbt instanceof CompoundTag queueTag) {
                List<ItemStack> list = new ArrayList<>();
                UUID id = queueTag.getUUID("Id");
                ListTag backpackList = queueTag.getList("Backpacks", Tag.TAG_COMPOUND);
                backpackList.forEach(nbt1 -> {
                    ItemStack.OPTIONAL_CODEC.parse(ops, nbt1)
                        .resultOrPartial(Constants.LOG::error)
                        .ifPresent(list::add);
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
            entry.putUUID("Id", id);
            entry.putLong("Pos", pos.asLong());
            shelves.add(entry);
        });
        tag.put("Shelves", shelves);

        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag queue = new ListTag();
        this.queue.forEach((id, backpacks) -> {
            CompoundTag queueTag = new CompoundTag();
            queueTag.putUUID("Id", id);
            ListTag backpackList = new ListTag();
            backpacks.forEach(backpack -> {
                ItemStack.OPTIONAL_CODEC.encodeStart(ops, backpack)
                    .resultOrPartial(Constants.LOG::error)
                    .ifPresent(backpackList::add);
            });
            queueTag.put("Backpacks", backpackList);
            queue.add(queueTag);
        });
        tag.put("Queue", queue);

        return tag;
    }

    public interface Access
    {
        Recall backpacked$getRecall();
    }
}
