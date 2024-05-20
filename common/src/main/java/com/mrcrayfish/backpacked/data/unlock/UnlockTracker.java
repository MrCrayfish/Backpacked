package com.mrcrayfish.backpacked.data.unlock;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.event.EventType;
import com.mrcrayfish.backpacked.event.entity.FeedAnimal;
import com.mrcrayfish.backpacked.util.Serializable;
import com.mrcrayfish.framework.api.sync.IDataSerializer;
import com.mrcrayfish.framework.entity.sync.Updatable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UnlockTracker implements Serializable
{
    public static final IDataSerializer<UnlockTracker> SERIALIZER = new Serializer();

    private final Updatable updatable;
    private final Set<ResourceLocation> unlockedBackpacks = new HashSet<>();
    private final Map<ResourceLocation, IProgressTracker> progressTrackerMap;

    public UnlockTracker(Updatable updatable)
    {
        this.updatable = updatable;
        if(updatable == Updatable.NULL) {
            // Don't create trackers on the client
            this.progressTrackerMap = ImmutableMap.of();
            return;
        }
        ImmutableMap.Builder<ResourceLocation, IProgressTracker> builder = ImmutableMap.builder();
        BackpackManager.instance().getBackpacks().forEach(backpack -> {
            IProgressTracker tracker = backpack.createProgressTracker();
            if(tracker != null) {
                builder.put(backpack.getId(), tracker);
            }
        });
        this.progressTrackerMap = builder.build();
    }

    public Set<ResourceLocation> getUnlockedBackpacks()
    {
        return Collections.unmodifiableSet(this.unlockedBackpacks);
    }

    public Map<ResourceLocation, IProgressTracker> getProgressTrackerMap()
    {
        return this.progressTrackerMap;
    }

    void setUnlockedBackpacks(Set<ResourceLocation> unlockedBackpacks)
    {
        this.unlockedBackpacks.clear();
        this.unlockedBackpacks.addAll(unlockedBackpacks);
    }

    public boolean isUnlocked(ResourceLocation id)
    {
        return this.unlockedBackpacks.contains(id);
    }

    public Optional<IProgressTracker> getProgressTracker(ResourceLocation id)
    {
        if(!Config.SERVER.common.unlockAllBackpacks.get() && !this.unlockedBackpacks.contains(id))
        {
            return Optional.ofNullable(this.progressTrackerMap.get(id));
        }
        return Optional.empty();
    }

    public boolean unlockBackpack(ResourceLocation id)
    {
        if(BackpackManager.instance().getBackpack(id) != null)
        {
            return this.unlockedBackpacks.add(id);
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
        this.progressTrackerMap.forEach((location, progressTracker) -> {
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
            IProgressTracker tracker = this.progressTrackerMap.get(id);
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
        @SuppressWarnings("removal")
        public UnlockTracker read(FriendlyByteBuf buf)
        {
            throw new UnsupportedOperationException("Use new method");
        }

        @Override
        public UnlockTracker read(Updatable updatable, FriendlyByteBuf buf)
        {
            UnlockTracker tracker = new UnlockTracker(updatable);
            Optional.ofNullable(buf.readNbt()).ifPresent(tracker::deserialize);
            return tracker;
        }

        @Override
        public Tag write(UnlockTracker value)
        {
            return value.serialize();
        }

        @Override
        @SuppressWarnings("removal")
        public UnlockTracker read(Tag nbt)
        {
            throw new UnsupportedOperationException("Use new method");
        }

        @Override
        public UnlockTracker read(Updatable updatable, Tag nbt)
        {
            UnlockTracker tracker = new UnlockTracker(updatable);
            if(nbt instanceof CompoundTag tag)
            {
                tracker.deserialize(tag);
            }
            return tracker;
        }
    }
}
