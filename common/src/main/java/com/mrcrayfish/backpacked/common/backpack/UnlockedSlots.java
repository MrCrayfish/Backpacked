package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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
    private final int nextCount;

    private UnlockedSlots()
    {
        this.slots = new HashSet<>();
        this.maxSlots = -1;
        this.nextCount = 0;
    }

    public UnlockedSlots(int maxSlots)
    {
        this(new HashSet<>(), maxSlots);
    }

    private UnlockedSlots(List<Integer> slots, int maxSlots)
    {
        this(new HashSet<>(slots), maxSlots);
    }

    private UnlockedSlots(Set<Integer> slots, int maxSlots)
    {
        assert maxSlots > 0;
        this.slots = slots;
        this.maxSlots = maxSlots;
        this.nextCount = calculateUnlockedCount(slots, maxSlots);
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
        Set<Integer> newSlots = new HashSet<>(this.slots);
        newSlots.add(slot);
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
        int maxLevelCost = 50; // TODO config
        float levelCost = maxLevelCost / (float) (totalSlots * totalSlots * totalSlots);
        levelCost = levelCost * (this.nextCount * this.nextCount * this.nextCount);
        levelCost = Math.clamp(levelCost + 0.5F, 1, maxLevelCost);
        return (int) levelCost;
    }

    /**
     * Calculates the next unlocked count. Unlocked slots are preserved even if the slot index
     * is greater than the maxSlots value. This can happen when a user changes the backpack size
     * in the config. The next unlocked count is used for calculating the experience cost.
     *
     * @param slots    the unlocked slots
     * @param maxSlots the maximum slot count
     * @return the next unlocked count
     */
    private static int calculateUnlockedCount(Set<Integer> slots, int maxSlots)
    {
        int count = 1;
        for(int slot : slots)
        {
            if(slot < maxSlots)
            {
                count++;
            }
        }
        return count;
    }
}
