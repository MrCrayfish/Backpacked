package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.InterpolateFunction;
import com.mrcrayfish.backpacked.common.SelectionFunction;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class UnlockableSlots
{
    public static final UnlockableSlots ALL = new UnlockableSlots();
    public static final UnlockableSlots NONE = new UnlockableSlots(0);

    public static final Codec<UnlockableSlots> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.list(Codec.INT).fieldOf("slots").forGetter(slots -> List.copyOf(slots.slots)),
        Codec.INT.fieldOf("maxSlots").forGetter(slots -> slots.maxSlots)
    ).apply(builder, UnlockableSlots::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockableSlots> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT.apply(ByteBufCodecs.collection(HashSet::new)), slots -> slots.slots,
        ByteBufCodecs.INT, slots -> slots.maxSlots,
        UnlockableSlots::new
    );

    public static final DataSerializer<UnlockableSlots> SERIALIZER = new DataSerializer<>(STREAM_CODEC, (obj, provider) -> {
        return CODEC.encodeStart(NbtOps.INSTANCE, obj).result().orElse(new CompoundTag());
    }, (tag, provider) -> {
        if(tag instanceof CompoundTag) {
            return CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
        }
        return null;
    });

    private final Set<Integer> slots;
    private final int maxSlots;
    private final int nextCount;

    private UnlockableSlots()
    {
        this.slots = new HashSet<>();
        this.maxSlots = -1;
        this.nextCount = 0;
    }

    public UnlockableSlots(int maxSlots)
    {
        this(new HashSet<>(), maxSlots);
    }

    private UnlockableSlots(List<Integer> slots, int maxSlots)
    {
        this(new HashSet<>(slots), maxSlots);
    }

    private UnlockableSlots(Set<Integer> slots, int maxSlots)
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
     * case where if the instance of UnlockableSlots is {@link #ALL}, then it will always return true.
     * Otherwise, it will look at the set of currently unlocked slot indexes.
     *
     * @param slot the index of the slot
     * @return true if the slot is unlocked
     */
    public boolean isUnlocked(int slot)
    {
        return this.maxSlots == -1 || slot >= 0 && slot < this.maxSlots && this.slots.contains(slot);
    }

    /**
     * Unlocks the slot at the given index. Keep in mind that this method returns a new immutable
     * UnlockableSlots. If the slot cannot be unlocked, or is already unlocked, this method will
     * simply return the instance of UnlockableSlots this method was invoked on. There is a special
     * case where if this UnlockableSlots is {@link #ALL}, it will always return {@link #ALL}.
     *
     * @param slot the index of the slot
     * @return a new UnlockableSlots instance, or the same UnlockableSlots if unable to unlock
     */
    public UnlockableSlots unlockSlot(int slot)
    {
        if(this.maxSlots == -1)
            return this;
        if(!this.isUnlockable(slot))
            return this;
        Set<Integer> newSlots = new HashSet<>(this.slots);
        newSlots.add(slot);
        return new UnlockableSlots(newSlots, this.maxSlots);
    }

    /**
     * Sets the amount of max slots. Only used for resizing so it matches the size of the backpack
     * slots. If the given maxSlots value is the same as the one currently on this object, it will
     * simply return itself there is no difference. The given maxSlots must be positive!
     *
     * @param maxSlots the new max slots
     * @return a new immutable UnlockableSlots object, or the same UnlockableSlots if the same value.
     */
    public UnlockableSlots setMaxSlots(int maxSlots)
    {
        if(this.maxSlots == -1 || this.maxSlots == maxSlots)
            return this;
        return new UnlockableSlots(this.slots, maxSlots);
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
     * Calculates the experience level cost to unlock a new slot. The cost is calculated based on
     * how many slots are already unlocked, generally getting more expensive the more slots that are
     * unlocked. Users can change the calculation options in the config of the mod, or even have all
     * slots unlocked by default.
     *
     * @return the experience level cost to unlock the next slot
     */
    public int nextUnlockCost(CostModel model, int numberOfSlots)
    {
        int totalCost = 0;
        for(int nextOffset = 0; nextOffset < numberOfSlots; nextOffset++)
        {
            if(model.useCustomCosts())
            {
                totalCost += this.getNextCustomCost(model.getCustomCosts(), model.getCustomCostsSelectionFunction(), nextOffset);
                continue;
            }
            int minLevelCost = model.getMinCost();
            int maxLevelCost = model.getMaxCost();
            float costNormal = this.nextCostNormal(maxLevelCost, model.getInterpolateFunction(), nextOffset);
            totalCost += (int) Mth.lerp(costNormal, minLevelCost, maxLevelCost);
        }
        return totalCost;
    }

    private int getNextCustomCost(List<Integer> costs, SelectionFunction selectionFunction, int countOffset)
    {
        if(!costs.isEmpty())
        {
            return switch(selectionFunction)
            {
                case LINEAR_INTERPOLATION ->
                {
                    float normal = Math.clamp((this.nextCount + countOffset) / (float) Math.max(1, this.maxSlots), 0, 1);
                    int index = (int) (costs.size() * (normal - 0.001F));
                    index = Mth.clamp(index, 0, costs.size() - 1);
                    yield Math.max(1, costs.get(index));
                }
                case INDEX_WITH_CLAMP ->
                {
                    yield costs.get(Mth.clamp(this.nextCount + countOffset - 1, 0, costs.size() - 1));
                }
            };
        }
        return 1;
    }

    private float nextCostNormal(int maxLevelCost, InterpolateFunction scaling, int countOffset)
    {
        int nextCount = this.nextCount + countOffset;
        int totalSlots = Math.max(1, this.maxSlots);
        return switch(scaling)
        {
            case LINEAR -> (float) nextCount / totalSlots;
            case SQUARED ->
            {
                float levelCost = maxLevelCost / (float) (totalSlots * totalSlots);
                levelCost = levelCost * (nextCount * nextCount);
                yield Math.clamp(levelCost + 0.5F, 1, maxLevelCost) / maxLevelCost;
            }
            case CUBIC ->
            {
                float levelCost = maxLevelCost / (float) (totalSlots * totalSlots * totalSlots);
                levelCost = levelCost * (nextCount * nextCount * nextCount);
                yield Math.clamp(levelCost + 0.5F, 1, maxLevelCost) / maxLevelCost;
            }
        };
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
        UnlockableSlots other = (UnlockableSlots) o;
        return this.maxSlots == other.maxSlots && this.slots.equals(other.slots);
    }
}
