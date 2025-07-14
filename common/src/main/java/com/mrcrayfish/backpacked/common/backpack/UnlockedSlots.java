package com.mrcrayfish.backpacked.common.backpack;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UnlockedSlots
{
    public static final UnlockedSlots ALL = new UnlockedSlots();

    public static final Codec<UnlockedSlots> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.list(Codec.INT).fieldOf("slots").forGetter(slots -> List.copyOf(slots.slots)),
        Codec.INT.fieldOf("maxSlots").forGetter(slots -> slots.maxSlots)
    ).apply(builder, UnlockedSlots::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockedSlots> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT.apply(ByteBufCodecs.collection(HashSet::new)), slots -> slots.slots,
        ByteBufCodecs.INT, slots -> slots.maxSlots,
        UnlockedSlots::new
    );

    private final Set<Integer> slots;
    private final int maxSlots;

    private UnlockedSlots()
    {
        this.slots = Collections.emptySet();
        this.maxSlots = -1;
    }

    public UnlockedSlots(int maxSlots)
    {
        this(Collections.emptySet(), maxSlots);
    }

    private UnlockedSlots(List<Integer> slots, int maxSlots)
    {
        this(ImmutableSet.copyOf(slots), maxSlots);
    }

    private UnlockedSlots(Set<Integer> slots, int maxSlots)
    {
        assert maxSlots > 0;
        this.slots = slots;
        this.maxSlots = maxSlots;
    }

    public int getMaxSlots()
    {
        return this.maxSlots;
    }

    public boolean isUnlocked(int slot)
    {
        return this.maxSlots == -1 || this.slots.contains(slot);
    }

    public UnlockedSlots unlockSlot(int slot)
    {
        if(this.maxSlots == -1)
            return this;
        if(!this.isUnlockable(slot))
            return this;
        Set<Integer> newSlots = ImmutableSet.<Integer>builder().addAll(this.slots).add(slot).build();
        return new UnlockedSlots(newSlots, this.maxSlots);
    }

    public UnlockedSlots setMaxSlots(int maxSlots)
    {
        return new UnlockedSlots(this.slots, maxSlots);
    }

    public int count()
    {
        return this.slots.size();
    }

    public boolean isUnlockable(int slot)
    {
        return slot >= 0 && slot < this.maxSlots && !this.slots.contains(slot);
    }

    public int nextUnlockCost()
    {
        int totalSlots = Math.max(1, this.maxSlots); // Prevents div by zero
        int maxExperienceLevelCost = 50; // TODO config
        int nextUnlockedCount = this.slots.size() + 1;
        float experienceLevelCost = maxExperienceLevelCost / (float) (totalSlots * totalSlots * totalSlots);
        experienceLevelCost = experienceLevelCost * (nextUnlockedCount * nextUnlockedCount * nextUnlockedCount);
        experienceLevelCost = Math.clamp(experienceLevelCost + 0.5F, 1, maxExperienceLevelCost);
        return (int) experienceLevelCost;
    }
}
