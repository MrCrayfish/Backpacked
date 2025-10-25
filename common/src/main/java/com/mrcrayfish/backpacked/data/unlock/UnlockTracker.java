package com.mrcrayfish.backpacked.data.unlock;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import com.mrcrayfish.framework.api.sync.SyncedObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.IntStream;

public class UnlockTracker extends SyncedObject
{
    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockTracker> STREAM_CODEC = StreamCodec.of(UnlockTracker::write, UnlockTracker::read);
    public static final DataSerializer<UnlockTracker> SERIALIZER = new DataSerializer<>(STREAM_CODEC, UnlockTracker::write, UnlockTracker::read);

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
        if(!Config.BACKPACK.cosmetics.unlockAllCosmetics.get() && !this.unlockedBackpacks.contains(backpackId))
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
            this.markDirty();
            return this.unlockedBackpacks.add(id);
        }
        return false;
    }

    private CompoundTag write(HolderLookup.Provider provider)
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

    private static UnlockTracker read(Tag tag, HolderLookup.Provider provider)
    {
        CompoundTag data = (CompoundTag) tag;
        UnlockTracker tracker = new UnlockTracker();

        ListTag unlockedBackpacks = data.getList("UnlockedBackpacks", Tag.TAG_STRING);
        unlockedBackpacks.forEach(t -> tracker.unlockedBackpacks.add(ResourceLocation.tryParse(t.getAsString())));

        ListTag progressTrackers = data.getList("ProgressTrackers", Tag.TAG_COMPOUND);
        progressTrackers.forEach(t -> {
            CompoundTag progressTag = (CompoundTag) t;
            ResourceLocation id = ResourceLocation.tryParse(progressTag.getString("Id"));
            IProgressTracker progressTracker = tracker.backpackToProgressTracker.get(id);
            if(progressTracker != null) {
                CompoundTag dataTag = progressTag.getCompound("Data");
                progressTracker.read(dataTag);
            }
        });
        return tracker;
    }

    private static void write(RegistryFriendlyByteBuf buf, UnlockTracker tracker)
    {
        buf.writeCollection(tracker.unlockedBackpacks, FriendlyByteBuf::writeResourceLocation);
        buf.writeVarInt(tracker.backpackToProgressTracker.size());
        tracker.backpackToProgressTracker.forEach((id, progressTracker) -> {
            buf.writeResourceLocation(id);
            CompoundTag tag = new CompoundTag();
            progressTracker.write(tag);
            buf.writeNbt(tag);
        });
    }

    private static UnlockTracker read(RegistryFriendlyByteBuf buf)
    {
        UnlockTracker tracker = new UnlockTracker();
        tracker.unlockedBackpacks.addAll(buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
        IntStream.range(0, buf.readVarInt()).forEach(value -> {
            ResourceLocation id = buf.readResourceLocation();
            CompoundTag tag = buf.readNbt();
            IProgressTracker progressTracker = tracker.backpackToProgressTracker.get(id);
            if(progressTracker != null) {
                progressTracker.read(tag);
            }
        });
        return tracker;
    }
}
