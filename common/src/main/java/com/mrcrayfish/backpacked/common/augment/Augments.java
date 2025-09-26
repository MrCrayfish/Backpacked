package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public record Augments(Augment<?> first, Augment<?> second, Augment<?> third)
{
    public static final Augments EMPTY = new Augments(EmptyAugment.INSTANCE, EmptyAugment.INSTANCE, EmptyAugment.INSTANCE);

    public static final Codec<Augments> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Augment.CODEC.fieldOf("first").forGetter(Augments::first),
        Augment.CODEC.fieldOf("second").forGetter(Augments::second),
        Augment.CODEC.fieldOf("third").forGetter(Augments::third)
    ).apply(builder, Augments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Augments> STREAM_CODEC = StreamCodec.composite(
        Augment.STREAM_CODEC, Augments::first,
        Augment.STREAM_CODEC, Augments::second,
        Augment.STREAM_CODEC, Augments::third,
        Augments::new
    );

    public Augments setFirst(Augment<?> augment)
    {
        return new Augments(augment, this.second, this.third);
    }

    public Augments setSecond(Augment<?> augment)
    {
        return new Augments(this.first, augment, this.third);
    }

    public Augments setThird(Augment<?> augment)
    {
        return new Augments(this.first, this.second, augment);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Augment<T>> T findAndCast(AugmentType<T> type)
    {
        if(this.first.type() == type)
            return (T) this.first;
        if(this.second.type() == type)
            return (T) this.second;
        if(this.third.type() == type)
            return (T) this.third;
        return null;
    }
}
