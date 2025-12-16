package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record Augments(Augment<?> firstAugment, boolean firstState, Augment<?> secondAugment, boolean secondState, Augment<?> thirdAugment, boolean thirdState)
{
    public static final Augments EMPTY = new Augments(EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true);

    public static final Codec<Augments> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Augment.CODEC.optionalFieldOf("first", EmptyAugment.INSTANCE).forGetter(Augments::firstAugment),
        Codec.BOOL.optionalFieldOf("firstState", true).forGetter(Augments::firstState),
        Augment.CODEC.optionalFieldOf("second", EmptyAugment.INSTANCE).forGetter(Augments::secondAugment),
        Codec.BOOL.optionalFieldOf("secondState", true).forGetter(Augments::secondState),
        Augment.CODEC.optionalFieldOf("third", EmptyAugment.INSTANCE).forGetter(Augments::thirdAugment),
        Codec.BOOL.optionalFieldOf("thirdState", true).forGetter(Augments::thirdState)
    ).apply(builder, Augments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Augments> STREAM_CODEC = StreamCodec.composite(
        Augment.STREAM_CODEC, Augments::firstAugment,
        ByteBufCodecs.BOOL, Augments::firstState,
        Augment.STREAM_CODEC, Augments::secondAugment,
        ByteBufCodecs.BOOL, Augments::secondState,
        Augment.STREAM_CODEC, Augments::thirdAugment,
        ByteBufCodecs.BOOL, Augments::thirdState,
        Augments::new
    );

    public Augments(Augment<?> firstAugment, boolean firstState, Augment<?> secondAugment, boolean secondState, Augment<?> thirdAugment, boolean thirdState)
    {
        this.firstAugment = firstAugment;
        this.firstState = firstState;
        this.secondAugment = secondAugment;
        this.secondState = secondState;
        this.thirdAugment = thirdAugment;
        this.thirdState = thirdState;
    }

    public Augment<?> getAugment(Position position)
    {
        return switch(position) {
            case FIRST -> restrict(this.firstAugment);
            case SECOND -> restrict(this.secondAugment);
            case THIRD -> restrict(this.thirdAugment);
        };
    }

    public Augments setAugment(Position position, Augment<?> augment)
    {
        return switch(position) {
            case FIRST -> new Augments(augment, this.firstState, this.secondAugment, this.secondState, this.thirdAugment, this.thirdState);
            case SECOND -> new Augments(this.firstAugment, this.firstState, augment, this.secondState, this.thirdAugment, this.thirdState);
            case THIRD -> new Augments(this.firstAugment, this.firstState, this.secondAugment, this.secondState, augment, this.thirdState);
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
            case FIRST -> new Augments(this.firstAugment, state, this.secondAugment, this.secondState, this.thirdAugment, this.thirdState);
            case SECOND -> new Augments(this.firstAugment, this.firstState, this.secondAugment, state, this.thirdAugment, this.thirdState);
            case THIRD -> new Augments(this.firstAugment, this.firstState, this.secondAugment, this.secondState, this.thirdAugment, state);
        };
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Augment<T>> T findEnabledAndCast(AugmentType<T> type)
    {
        if(Config.getDisabledAugments().contains(type.id()))
            return null;
        if(this.firstState && this.firstAugment.type() == type)
            return (T) this.firstAugment;
        if(this.secondState && this.secondAugment.type() == type)
            return (T) this.secondAugment;
        if(this.thirdState && this.thirdAugment.type() == type)
            return (T) this.thirdAugment;
        return null;
    }

    public <T extends Augment<T>> boolean has(AugmentType<T> type)
    {
        if(Config.getDisabledAugments().contains(type.id()))
            return false;
        return this.firstAugment.type() == type || this.secondAugment.type() == type || this.thirdAugment.type() == type;
    }

    public enum Position
    {
        FIRST, SECOND, THIRD;

        public static final StreamCodec<FriendlyByteBuf, Position> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Position.class));
    }

    public static Augments get(ItemStack stack)
    {
        return stack.getOrDefault(ModDataComponents.AUGMENTS.get(), Augments.EMPTY);
    }

    public static void set(ItemStack stack, Augments augments)
    {
        stack.set(ModDataComponents.AUGMENTS.get(), augments);
    }

    public static Augment<?> restrict(Augment<?> augment)
    {
        if(Config.getDisabledAugments().contains(augment.type().id()))
        {
            return EmptyAugment.INSTANCE;
        }
        return augment;
    }
}
