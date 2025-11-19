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

    private final ServerLevel level;
    private final BiMap<UUID, BlockPos> shelves = HashBiMap.create();
    private final Map<UUID, List<PlayerBackpack>> recalling = new HashMap<>();
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
        this.flushQueueById(shelf.id(), shelf.getBlockPos().getCenter());
        this.recalling.remove(shelf.id());
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
            this.recalling.computeIfAbsent(shelfId, k -> new ArrayList<>()).add(new PlayerBackpack(player.getUUID(), backpack.copyAndClear()));
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
        var it = this.recalling.entrySet().iterator();
        while(it.hasNext())
        {
            List<PlayerBackpack> playerBackpacks = it.next().getValue();
            count[0] += this.flushQueue(server, playerBackpacks, null);
            it.remove();
        }
        return count[0];
    }

    private int flushQueue(MinecraftServer server, List<PlayerBackpack> playerBackpacks, @Nullable Vec3 overridePos)
    {
        int[] count = {0};
        var it = playerBackpacks.iterator();
        while(it.hasNext()) {
            PlayerBackpack backpack = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(backpack.owner);
            if(player == null)
                continue;
            Vec3 pos = overridePos != null ? overridePos : player.position();
            ItemEntity entity = new ItemEntity(this.level, pos.x, pos.y, pos.z, backpack.stack);
            entity.setDefaultPickUpDelay();
            entity.setUnlimitedLifetime();
            player.level().addFreshEntity(entity);
            it.remove();
            count[0]++;
        }
        return count[0];
    }

    private void flushQueueById(UUID shelfId, @Nullable Vec3 overridePos)
    {
        List<PlayerBackpack> playerBackpacks = this.recalling.remove(shelfId);
        if(playerBackpacks != null)
        {
            this.flushQueue(this.level.getServer(), playerBackpacks, overridePos);
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
                this.flushQueueById(entry.getKey(), blockPos.getCenter());
                this.setDirty();
                continue;
            }

            // Ensure the shelf is registered
            this.registerShelf(shelf);

            List<PlayerBackpack> backpacks = this.recalling.get(shelf.id());
            if(backpacks == null)
                continue;

            for(PlayerBackpack backpack : backpacks)
            {
                if(shelf.getBackpack().isEmpty())
                {
                    // Place backpack on the shelf if it was empty
                    shelf.setBackpack(backpack.stack.copy());
                }
                else
                {
                    // Otherwise spawn the backpack into the world at the shelf position
                    Vec3 center = blockPos.getCenter();
                    ItemEntity entity = new ItemEntity(this.level, center.x, center.y, center.z, backpack.stack.copy());
                    entity.setUnlimitedLifetime(); // Ensure backpack doesn't despawn
                    entity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(entity);
                }
                this.setDirty();
            }

            // Finally remove the queue since it is now empty
            this.recalling.remove(shelf.id());
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
        ListTag recallingList = tag.getList("Recalling", Tag.TAG_COMPOUND);
        recallingList.forEach(nbt -> {
            if(nbt instanceof CompoundTag entryTag) {
                UUID id;
                try {
                     id = entryTag.getUUID("ShelfId");
                } catch(Exception e) {
                    Constants.LOG.error("Missing ShelfId when reading Recall queue", e);
                    return;
                }
                List<PlayerBackpack> list = new ArrayList<>();
                ListTag backpackList = entryTag.getList("PlayerBackpacks", Tag.TAG_COMPOUND);
                backpackList.forEach(nbt1 -> {
                    if(nbt1 instanceof CompoundTag backpackTag) {
                        try {
                            UUID owner = backpackTag.getUUID("Owner");
                            ItemStack stack = ItemStack.OPTIONAL_CODEC.parse(ops, backpackTag.get("BackpackStack"))
                                .resultOrPartial(Constants.LOG::error)
                                .orElse(ItemStack.EMPTY);
                            if(!stack.isEmpty()) {
                                list.add(new PlayerBackpack(owner, stack));
                            } else {
                                Constants.LOG.warn("Skipping Recall queue entry since the stack is empty");
                            }
                        } catch(Exception e) {
                            Constants.LOG.error("An error occurred while reading Recall queue entry", e);
                        }
                    }
                });
                recall.recalling.put(id, list);
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
        ListTag recallingList = new ListTag();
        this.recalling.forEach((id, backpacks) -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("ShelfId", id);
            ListTag backpackList = new ListTag();
            backpacks.forEach(backpack -> {
                CompoundTag backpackTag = new CompoundTag();
                backpackTag.putUUID("Owner", backpack.owner);
                ItemStack.OPTIONAL_CODEC.encodeStart(ops, backpack.stack)
                    .resultOrPartial(Constants.LOG::error)
                    .ifPresent(t -> backpackTag.put("BackpackStack", t));
                backpackList.add(backpackTag);
            });
            entryTag.put("PlayerBackpacks", backpackList);
            recallingList.add(entryTag);
        });
        tag.put("Recalling", recallingList);

        return tag;
    }

    private record PlayerBackpack(UUID owner, ItemStack stack) {}

    public interface Access
    {
        Recall backpacked$getRecall();
    }
}
