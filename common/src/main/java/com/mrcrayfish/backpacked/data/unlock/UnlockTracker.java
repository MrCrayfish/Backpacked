package com.mrcrayfish.backpacked.data.unlock;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.util.Serializable;
import com.mrcrayfish.framework.api.sync.IDataSerializer;
import com.mrcrayfish.framework.api.sync.SyncedObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UnlockTracker extends SyncedObject implements Serializable
{
    public static final Serializer SERIALIZER = new Serializer();

    private final Set<ResourceLocation> unlockedBackpacks = new HashSet<>();
    private final Map<ResourceLocation, IProgressTracker> backpackToProgressTracker;
    private final Map<Class<?>, List<IProgressTracker>> classToProgressTrackers;

    public UnlockTracker()
    {
        Map<ResourceLocation, IProgressTracker> backpackMap = new HashMap<>();
        Map<Class<?>, List<IProgressTracker>> classMap = new HashMap<>();
        BackpackManager.instance().getBackpacks().forEach(backpack -> {
            IProgressTracker tracker = backpack.createProgressTracker(backpack.getId());
            if(tracker != null) {
                classMap.computeIfAbsent(tracker.getClass(), c -> new ArrayList<>()).add(tracker);
                backpackMap.put(backpack.getId(), tracker);
            }
        });
        this.backpackToProgressTracker = ImmutableMap.copyOf(backpackMap);
        this.classToProgressTrackers = ImmutableMap.copyOf(classMap);
    }

    public Set<ResourceLocation> getUnlockedBackpacks()
    {
        return Collections.unmodifiableSet(this.unlockedBackpacks);
    }

    public Map<ResourceLocation, IProgressTracker> getProgressTrackerMap()
    {
        return this.backpackToProgressTracker;
    }

    public boolean isUnlocked(ResourceLocation id)
    {
        return this.unlockedBackpacks.contains(id);
    }

    public Optional<IProgressTracker> getProgressTracker(ResourceLocation backpackId)
    {
        if(!Config.SERVER.backpack.unlockAllCosmetics.get() && !this.unlockedBackpacks.contains(backpackId))
        {
            return Optional.ofNullable(this.backpackToProgressTracker.get(backpackId));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getProgressTrackers(Class<T> trackerClass)
    {
        if(this.classToProgressTrackers.containsKey(trackerClass))
        {
            return (List<T>) this.classToProgressTrackers.get(trackerClass);
        }
        return Collections.emptyList();
    }

    public boolean unlockBackpack(ResourceLocation id)
    {
        if(BackpackManager.instance().getBackpack(id) != null)
        {
            if(this.unlockedBackpacks.add(id))
            {
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag serialize()
    {
        CompoundTag tag = new CompoundTag();

        ListTag unlockedBackpacks = new ListTag();
        this.unlockedBackpacks.forEach(location -> unlockedBackpacks.add(StringTag.valueOf(location.toString())));
        tag.put("UnlockedBackpacks", unlockedBackpacks);

        ListTag progressTrackers = new ListTag();
        this.backpackToProgressTracker.forEach((location, progressTracker) -> {
            CompoundTag progressTag = new CompoundTag();
            progressTag.putString("Id", location.toString());
            CompoundTag dataTag = new CompoundTag();
            progressTracker.write(dataTag);
            progressTag.put("Data", dataTag);
            progressTrackers.add(progressTag);
        });
        tag.put("ProgressTrackers", progressTrackers);

        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag)
    {
        this.unlockedBackpacks.clear();

        ListTag unlockedBackpacks = tag.getList("UnlockedBackpacks", Tag.TAG_STRING);
        unlockedBackpacks.forEach(t -> this.unlockedBackpacks.add(ResourceLocation.tryParse(t.getAsString())));

        ListTag progressTrackers = tag.getList("ProgressTrackers", Tag.TAG_COMPOUND);
        progressTrackers.forEach(t ->
        {
            CompoundTag progressTag = (CompoundTag) t;
            ResourceLocation id = new ResourceLocation(progressTag.getString("Id"));
            IProgressTracker tracker = this.backpackToProgressTracker.get(id);
            if(tracker != null)
            {
                CompoundTag dataTag = progressTag.getCompound("Data");
                tracker.read(dataTag);
            }
        });
    }

    public static class Serializer implements IDataSerializer<UnlockTracker>
    {
        @Override
        public void write(FriendlyByteBuf buf, UnlockTracker value)
        {
            buf.writeNbt(value.serialize());
        }

        @Override
        public UnlockTracker read(FriendlyByteBuf buf)
        {
            UnlockTracker tracker = new UnlockTracker();
            Optional.ofNullable(buf.readNbt()).ifPresent(tracker::deserialize);
            return tracker;
        }

        @Override
        public Tag write(UnlockTracker value)
        {
            return value.serialize();
        }

        @Override
        public UnlockTracker read(Tag nbt)
        {
            UnlockTracker tracker = new UnlockTracker();
            if(nbt instanceof CompoundTag tag)
            {
                tracker.deserialize(tag);
            }
            return tracker;
        }
    }
}
