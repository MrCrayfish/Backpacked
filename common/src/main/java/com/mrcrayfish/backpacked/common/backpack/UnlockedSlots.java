package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    /**
     * @return The maximum amount of slots that can be unlocked
     */
    public int getMaxSlots()
    {
        return this.maxSlots;
    }

    /**
     * Determines if the slot at the given index has been unlocked. This method includes a special
     * case where if the instance of UnlockedSlots is {@link #ALL}, then it will always return true.
     * Otherwise, it will look at the set of currently unlocked slot indexes.
     *
     * @param slot the index of the slot
     * @return true if the slot is unlocked
     */
    public boolean isUnlocked(int slot)
    {
        return this.maxSlots == -1 || this.slots.contains(slot);
    }

    /**
     * Unlocks the slot at the given index. Keep in mind that this method returns a new immutable
     * UnlockedSlots. If the slot cannot be unlocked, or is already unlocked, this method will
     * simply return the instance of UnlockedSlots this method was invoked on. There is a special
     * case where if this UnlockedSlots is {@link #ALL}, it will always return {@link #ALL}.
     *
     * @param slot the index of the slot
     * @return a new UnlockedSlots instance, or the same UnlockedSlots if unable to unlock
     */
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

    /**
     * Sets the amount of max slots. Only used for resizing so it matches the size of the backpack
     * slots. If the given maxSlots value is the same as the one currently on this object, it will
     * simply return itself there is no difference. The given maxSlots must be positive!
     *
     * @param maxSlots the new max slots
     * @return a new immutable UnlockedSlots object, or the same UnlockedSlots if the same value.
     */
    public UnlockedSlots setMaxSlots(int maxSlots)
    {
        if(this.maxSlots == maxSlots)
            return this;
        return new UnlockedSlots(this.slots, maxSlots);
    }

    /**
     * Determines if the given slot index is able to be unlocked. If the index is out of bounds,
     * or the slot is already unlocked, the method will simply return false.
     *
     * @param slot the index of the slot
     * @return true if able to be unlocked, otherwise false
     */
    public boolean isUnlockable(int slot)
    {
        return slot >= 0 && slot < this.maxSlots && !this.slots.contains(slot);
    }

    /**
     * Calculates the experience level cost to unlock a new slot in the backpack inventory. The cost
     * is calculated based on how many slots are already unlocked, generally getting more expensive
     * the more slots that are unlocked. Users can change the calculation options in the config of
     * the mod, or even have all slots unlocked by default (TODO).
     *
     * @return the experience level cost to unlock the next slot
     */
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

    @Override
    public int hashCode()
    {
        return Objects.hash(this.slots, this.maxSlots);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
            return false;
        UnlockedSlots other = (UnlockedSlots) o;
        return this.maxSlots == other.maxSlots && this.slots.equals(other.slots);
    }
}
