package com.mrcrayfish.backpacked.data.unlock;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import com.mrcrayfish.framework.api.sync.SyncedObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.*;
import java.util.stream.IntStream;

public class UnlockTracker extends SyncedObject
{
    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockTracker> STREAM_CODEC = StreamCodec.of(UnlockTracker::encode, UnlockTracker::decode);
    public static final DataSerializer<UnlockTracker> SERIALIZER = new DataSerializer<>(STREAM_CODEC, (output, unlockTracker) -> {
        ValueOutput.TypedOutputList<Identifier> ids = output.list("UnlockedBackpacks", Identifier.CODEC);
        unlockTracker.unlockedBackpacks.forEach(ids::add);
        ValueOutput.ValueOutputList trackers = output.childrenList("ProgressTrackers");
        unlockTracker.backpackToProgressTracker.forEach((id, progressTracker) -> {
            ValueOutput subOutput = trackers.addChild();
            subOutput.store("Id", Identifier.CODEC, id);
            progressTracker.write(subOutput.child("Data"));
        });
    }, input -> {
        UnlockTracker unlockTracker = new UnlockTracker();
        input.list("UnlockedBackpacks", Identifier.CODEC).ifPresent(ids -> {
            ids.forEach(unlockTracker.unlockedBackpacks::add);
        });
        input.childrenList("ProgressTrackers").ifPresent(trackers -> {
            trackers.forEach(subInput -> {
                subInput.read("Id", Identifier.CODEC).ifPresent(id -> {
                    IProgressTracker progressTracker = unlockTracker.backpackToProgressTracker.get(id);
                    if(progressTracker != null) {
                        progressTracker.read(subInput.childOrEmpty("Data"));
                    }
                });
            });
        });
        unlockTracker.removeCompletedProgressTrackers();
        return unlockTracker;
    });

    private final HashSet<Identifier> unlockedBackpacks = new HashSet<>();
    private final Map<Identifier, IProgressTracker> backpackToProgressTracker;
    private final Map<Class<?>, List<IProgressTracker>> classToIncompleteProgressTrackers;

    public UnlockTracker()
    {
        Map<Identifier, IProgressTracker> backpackMap = new HashMap<>();
        Map<Class<?>, List<IProgressTracker>> classMap = new HashMap<>();
        BackpackManager.instance().getBackpacks().forEach(backpack -> {
            IProgressTracker tracker = backpack.createProgressTracker(backpack.getId());
            if(tracker != null) {
                classMap.computeIfAbsent(tracker.getClass(), c -> new ArrayList<>()).add(tracker);
                backpackMap.put(backpack.getId(), tracker);
            }
        });
        this.backpackToProgressTracker = ImmutableMap.copyOf(backpackMap);
        this.classToIncompleteProgressTrackers = ImmutableMap.copyOf(classMap);
    }

    public Set<Identifier> getUnlockedBackpacks()
    {
        return Collections.unmodifiableSet(this.unlockedBackpacks);
    }

    public Map<Identifier, IProgressTracker> getProgressTrackerMap()
    {
        return this.backpackToProgressTracker;
    }

    public boolean isUnlocked(Identifier id)
    {
        return this.unlockedBackpacks.contains(id);
    }

    public Optional<IProgressTracker> getProgressTracker(Identifier backpackId)
    {
        if(!Config.BACKPACK.cosmetics.unlockAllCosmetics.get() && !this.unlockedBackpacks.contains(backpackId))
        {
            return Optional.ofNullable(this.backpackToProgressTracker.get(backpackId));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getIncompleteProgressTrackers(Class<T> trackerClass)
    {
        if(this.classToIncompleteProgressTrackers.containsKey(trackerClass))
        {
            return Collections.unmodifiableList((List<T>) this.classToIncompleteProgressTrackers.get(trackerClass));
        }
        return Collections.emptyList();
    }

    private void removeCompletedProgressTracker(Identifier id)
    {
        IProgressTracker tracker = this.backpackToProgressTracker.get(id);
        if(tracker != null && tracker.isComplete())
        {
            this.classToIncompleteProgressTrackers.get(tracker.getClass()).remove(tracker);
        }
    }

    private void removeCompletedProgressTrackers()
    {
        for(Identifier id : this.unlockedBackpacks)
        {
            this.removeCompletedProgressTracker(id);
        }
    }

    public boolean unlockBackpack(Identifier id)
    {
        if(BackpackManager.instance().getBackpack(id) != null)
        {
            this.markDirty();
            this.removeCompletedProgressTracker(id);
            return this.unlockedBackpacks.add(id);
        }
        return false;
    }

    private static void encode(RegistryFriendlyByteBuf buf, UnlockTracker tracker)
    {
        buf.writeCollection(tracker.unlockedBackpacks, FriendlyByteBuf::writeIdentifier);
        buf.writeVarInt(tracker.backpackToProgressTracker.size());
        tracker.backpackToProgressTracker.forEach((id, progressTracker) -> {
            buf.writeIdentifier(id);
            ProblemReporter.Collector collector = new ProblemReporter.Collector();
            TagValueOutput output = TagValueOutput.createWithContext(collector, buf.registryAccess());
            progressTracker.write(output);
            buf.writeNbt(output.buildResult());
        });
    }

    private static UnlockTracker decode(RegistryFriendlyByteBuf buf)
    {
        UnlockTracker unlockTracker = new UnlockTracker();
        unlockTracker.unlockedBackpacks.addAll(buf.readCollection(HashSet::new, FriendlyByteBuf::readIdentifier));
        IntStream.range(0, buf.readVarInt()).forEach(value -> {
            Identifier id = buf.readIdentifier();
            CompoundTag tag = buf.readNbt();
            IProgressTracker progressTracker = unlockTracker.backpackToProgressTracker.get(id);
            if(progressTracker != null && tag != null) {
                ProblemReporter.Collector collector = new ProblemReporter.Collector();
                ValueInput input = TagValueInput.create(collector, buf.registryAccess(), tag);
                progressTracker.read(input);
            }
        });
        unlockTracker.removeCompletedProgressTrackers();
        return unlockTracker;
    }
}
