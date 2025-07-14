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

public class UnlockedSlots
{
    public static final UnlockedSlots EMPTY = new UnlockedSlots();

    public static final Codec<UnlockedSlots> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.list(Codec.INT).fieldOf("slots").forGetter(slots -> List.copyOf(slots.slots))
    ).apply(builder, UnlockedSlots::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockedSlots> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT.apply(ByteBufCodecs.collection(HashSet::new)), slots -> slots.slots,
        UnlockedSlots::new
    );

    private final Set<Integer> slots;

    private UnlockedSlots()
    {
        this(Collections.emptySet());
    }

    private UnlockedSlots(Set<Integer> slots)
    {
        this.slots = slots;
    }

    public UnlockedSlots(List<Integer> slots)
    {
        this.slots = ImmutableSet.copyOf(slots);
    }

    public boolean isUnlocked(int slot)
    {
        return this.slots.contains(slot);
    }

    public UnlockedSlots unlockSlot(int slot)
    {
        // TODO check max range
        if(slot < 0 || this.slots.contains(slot))
            return this;
        return new UnlockedSlots(ImmutableSet.<Integer>builder()
                .addAll(this.slots).add(slot)
                .build());
    }
}
