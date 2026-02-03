package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.InterpolateFunction;
import com.mrcrayfish.backpacked.common.SelectionFunction;
import com.mrcrayfish.framework.api.sync.IDataSerializer;
import com.mrcrayfish.framework.api.sync.SyncedObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public sealed class UnlockableSlots extends SyncedObject
{
    private static final UnlockableSlots ALL = new UnlockableSlots();
    private static final UnlockableSlots NONE = new UnlockableSlots(0);

    public static final Codec<UnlockableSlots> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.list(Codec.INT).fieldOf("slots").forGetter(slots -> List.copyOf(slots.slots)),
        Codec.INT.fieldOf("maxSlots").forGetter(slots -> slots.maxSlots)
    ).apply(builder, UnlockableSlots::new));

    public static final IDataSerializer<UnlockableSlots> SERIALIZER = new IDataSerializer<>()
    {
        @Override
        public void write(FriendlyByteBuf buf, UnlockableSlots value)
        {
            value.encode(buf);
        }

        @Override
        public UnlockableSlots read(FriendlyByteBuf buf)
        {
            return UnlockableSlots.decode(buf);
        }

        @Override
        public Tag write(UnlockableSlots value)
        {
            return CODEC.encodeStart(NbtOps.INSTANCE, value).result().orElse(new CompoundTag());
        }

        @Override
        public UnlockableSlots read(Tag nbt)
        {
            return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(null);
        }
    };

    protected Set<Integer> slots;
    protected int maxSlots;
    protected int nextCount;

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
        this.slots = slots;
        this.maxSlots = maxSlots;
        this.recalculateUnlockedCount();
    }

    public int[] getSlotsArray()
    {
        return this.slots.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * @return The maximum amount of slots that can be unlocked
     */
    public int getMaxSlots()
    {
        return this.maxSlots;
    }

    /**
     * @return The count of slots unlocked
     */
    public int getUnlockCount()
    {
        return this.slots.size();
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
    public void unlockSlot(int slot)
    {
        if(this.maxSlots == -1)
            return;
        if(!this.isUnlockable(slot))
            return;
        this.slots.add(slot);
        this.recalculateUnlockedCount();
        this.markDirty();
    }

    /**
     * Unlocks the slots at the given indexes. Keep in mind that this method returns a new immutable
     * UnlockableSlots. If a slot cannot be unlocked or is already unlocked, it will simply be skipped.
     * There is a special case where if this UnlockableSlots is {@link #ALL}, it will always return
     * {@link #ALL}.
     *
     * @param slots a list of slot indexes
     * @return a new UnlockableSlots instance, or the same UnlockableSlots if unable to unlock
     */
    public void unlockSlots(List<Integer> slots)
    {
        if(this.maxSlots == -1)
            return;
        slots.forEach(slot -> {
            if(this.isUnlockable(slot)) {
                this.slots.add(slot);
            }
        });
        this.recalculateUnlockedCount();
        this.markDirty();
    }

    /**
     * Sets the amount of max slots. Only used for resizing so it matches the size of the backpack
     * slots. If the given maxSlots value is the same as the one currently on this object, it will
     * simply return itself there is no difference. The given maxSlots must be positive!
     *
     * @param maxSlots the new max slots
     * @return a new immutable UnlockableSlots object, or the same UnlockableSlots if the same value.
     */
    public void setMaxSlots(int maxSlots)
    {
        if(this.maxSlots == -1 || maxSlots < 0 || this.maxSlots == maxSlots)
            return;
        this.maxSlots = maxSlots;
        this.recalculateUnlockedCount();
        this.markDirty();
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
                    float normal = Mth.clamp((this.nextCount + countOffset) / (float) Math.max(1, this.getMaxSlots()), 0, 1);
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
        int totalSlots = Math.max(1, this.getMaxSlots());
        return switch(scaling)
        {
            case LINEAR -> (float) nextCount / totalSlots;
            case SQUARED ->
            {
                float levelCost = maxLevelCost / (float) (totalSlots * totalSlots);
                levelCost = levelCost * (nextCount * nextCount);
                yield Mth.clamp(levelCost + 0.5F, 1, maxLevelCost) / maxLevelCost;
            }
            case CUBIC ->
            {
                float levelCost = maxLevelCost / (float) (totalSlots * totalSlots * totalSlots);
                levelCost = levelCost * (nextCount * nextCount * nextCount);
                yield Mth.clamp(levelCost + 0.5F, 1, maxLevelCost) / maxLevelCost;
            }
        };
    }

    /**
     * Calculates the next unlocked count. Unlocked slots are preserved even if the slot index
     * is greater than the maxSlots value. This can happen when a user changes the backpack size
     * in the config. The next unlocked count is used for calculating the experience cost.
     */
    protected void recalculateUnlockedCount()
    {
        int count = 1;
        for(int slot : this.slots)
        {
            if(slot < this.maxSlots)
            {
                count++;
            }
        }
        this.nextCount = count;
        this.markDirty();
    }

    public static UnlockableSlots get(ItemStack stack, String key)
    {
        return new View(stack, key);
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeVarIntArray(this.getSlotsArray());
        buf.writeInt(this.getMaxSlots());
    }

    public static UnlockableSlots decode(FriendlyByteBuf buf)
    {
        int[] slots = buf.readVarIntArray();
        int maxSlots = buf.readInt();
        return new UnlockableSlots(Arrays.stream(slots).boxed().toList(), maxSlots);
    }

    public static UnlockableSlots all()
    {
        return ALL;
    }

    public static UnlockableSlots none()
    {
        return NONE.copy();
    }

    public UnlockableSlots copy()
    {
        return new UnlockableSlots(Arrays.stream(this.getSlotsArray()).boxed().toList(), this.getMaxSlots());
    }

    public void update(ItemStack backpack, String key)
    {
        if(this.getMaxSlots() == -1 || this == ALL)
            return;
        CompoundTag tag = backpack.getOrCreateTag();
        CompoundTag unlockedTag = new CompoundTag();
        unlockedTag.putInt("MaxSlots", this.getMaxSlots());
        unlockedTag.putIntArray("Slots", this.getSlotsArray());
        tag.put(key, unlockedTag);
    }

    private static final class View extends UnlockableSlots
    {
        private final ItemStack stack;
        private final String key;

        public View(ItemStack stack, String key)
        {
            this.stack = stack;
            this.key = key;
        }

        @Nullable
        private CompoundTag getTag()
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(tag.contains(this.key, Tag.TAG_COMPOUND))
            {
                return tag.getCompound(this.key);
            }
            return null;
        }

        private CompoundTag getOrCreateTag()
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(!tag.contains(this.key, Tag.TAG_COMPOUND))
            {
                tag.put(this.key, new CompoundTag());
            }
            return tag.getCompound(this.key);
        }

        @Override
        public int[] getSlotsArray()
        {
            CompoundTag tag = this.getTag();
            if(tag != null && tag.contains("Slots", Tag.TAG_INT_ARRAY))
            {
                return tag.getIntArray("Slots");
            }
            return new int[0];
        }

        @Override
        public int getMaxSlots()
        {
            CompoundTag tag = this.getTag();
            return tag != null ? Math.max(0, tag.getInt("MaxSlots")) : 0;
        }

        @Override
        public int getUnlockCount()
        {
            return this.getSlotsArray().length;
        }

        @Override
        public boolean isUnlocked(int slot)
        {
            if(slot < 0 || slot >= this.getMaxSlots())
                return false;
            CompoundTag tag = this.getTag();
            if(tag == null)
                return false;
            if(!tag.contains("Slots", Tag.TAG_INT_ARRAY))
                return false;
            int[] slots = tag.getIntArray("Slots");
            for(int s : slots)
            {
                if(s == slot)
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void unlockSlot(int slot)
        {
            if(!this.isUnlockable(slot))
                return;
            CompoundTag tag = this.getOrCreateTag();
            int[] slots = tag.getIntArray("Slots");
            int[] newSlots = new int[slots.length + 1];
            System.arraycopy(slots, 0, newSlots, 0, slots.length);
            newSlots[slots.length] = slot;
            Arrays.sort(newSlots);
            tag.putIntArray("Slots", newSlots);
            this.recalculateUnlockedCount();
            this.markDirty();
        }

        @Override
        public void unlockSlots(List<Integer> unlockSlots)
        {
            List<Integer> validSlots = unlockSlots.stream().filter(this::isUnlockable).toList();
            CompoundTag tag = this.getOrCreateTag();
            int[] slots = tag.getIntArray("Slots");
            int[] newSlots = new int[slots.length + validSlots.size()];
            System.arraycopy(slots, 0, newSlots, 0, slots.length);
            for(int i = 0; i < validSlots.size(); i++)
            {
                newSlots[slots.length + i] = validSlots.get(i);
            }
            Arrays.sort(newSlots);
            tag.putIntArray("Slots", newSlots);
            this.recalculateUnlockedCount();
            this.markDirty();
        }

        @Override
        public void setMaxSlots(int maxSlots)
        {
            if(maxSlots < 0)
                return;
            CompoundTag tag = this.getOrCreateTag();
            tag.putInt("MaxSlots", maxSlots);
            this.recalculateUnlockedCount();
            this.markDirty();
        }

        @Override
        public boolean isUnlockable(int slot)
        {
            return slot >= 0 && slot < this.getMaxSlots() && !this.isUnlocked(slot);
        }

        @Override
        protected void recalculateUnlockedCount()
        {
            int count = 1;
            CompoundTag tag = this.getTag();
            if(tag != null && tag.contains("Slots", Tag.TAG_INT_ARRAY))
            {
                int maxSlots = this.getMaxSlots();
                int[] slots = tag.getIntArray("Slots");
                for(int slot : slots)
                {
                    if(slot < maxSlots)
                    {
                        count++;
                    }
                }

            }
            this.nextCount = count;
            this.markDirty();
        }
    }
}
