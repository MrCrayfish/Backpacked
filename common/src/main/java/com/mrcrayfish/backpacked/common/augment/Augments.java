package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public record Augments(Augment<?> first, boolean firstState, Augment<?> second, boolean secondState, Augment<?> third, boolean thirdState)
{
    public static final Augments EMPTY = new Augments(EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true);

    public static final Codec<Augments> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Augment.CODEC.fieldOf("first").forGetter(Augments::first),
        Codec.BOOL.fieldOf("firstState").forGetter(Augments::firstState),
        Augment.CODEC.fieldOf("second").forGetter(Augments::second),
        Codec.BOOL.fieldOf("secondState").forGetter(Augments::secondState),
        Augment.CODEC.fieldOf("third").forGetter(Augments::third),
        Codec.BOOL.fieldOf("thirdState").forGetter(Augments::thirdState)
    ).apply(builder, Augments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Augments> STREAM_CODEC = StreamCodec.composite(
        Augment.STREAM_CODEC, Augments::first,
        ByteBufCodecs.BOOL, Augments::firstState,
        Augment.STREAM_CODEC, Augments::second,
        ByteBufCodecs.BOOL, Augments::secondState,
        Augment.STREAM_CODEC, Augments::third,
        ByteBufCodecs.BOOL, Augments::thirdState,
        Augments::new
    );

    public Augment<?> getAugment(Position position)
    {
        return switch(position) {
            case FIRST -> this.first;
            case SECOND -> this.second;
            case THIRD -> this.third;
        };
    }

    public Augments setAugment(Position position, Augment<?> augment)
    {
        return switch(position) {
            case FIRST -> new Augments(augment, this.firstState, this.second, this.secondState, this.third, this.thirdState);
            case SECOND -> new Augments(this.first, this.firstState, augment, this.secondState, this.third, this.thirdState);
            case THIRD -> new Augments(this.first, this.firstState, this.second, this.secondState, augment, this.thirdState);
        };
    }

    public boolean getState(Position position)
    {
        return switch(position) {
            case FIRST -> this.firstState;
            case SECOND -> this.secondState;
            case THIRD -> this.thirdState;
        };
    }

    public Augments setState(Position position, boolean state)
    {
        return switch(position) {
            case FIRST -> new Augments(this.first, state, this.second, this.secondState, this.third, this.thirdState);
            case SECOND -> new Augments(this.first, this.firstState, this.second, state, this.third, this.thirdState);
            case THIRD -> new Augments(this.first, this.firstState, this.second, this.secondState, this.third, state);
        };
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

    public enum Position
    {
        FIRST, SECOND, THIRD;
    }
}
