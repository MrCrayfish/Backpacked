package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public Augment<?> getAugment(Position position)
    {
        return switch(position) {
            case FIRST -> this.firstAugment;
            case SECOND -> this.secondAugment;
            case THIRD -> this.thirdAugment;
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
    public <T extends Augment<T>> T findAndCast(AugmentType<T> type)
    {
        if(this.firstAugment.type() == type)
            return (T) this.firstAugment;
        if(this.secondAugment.type() == type)
            return (T) this.secondAugment;
        if(this.thirdAugment.type() == type)
            return (T) this.thirdAugment;
        return null;
    }

    public List<ResourceLocation> toTypeIds()
    {
        return List.of(this.firstAugment.type().id(), this.secondAugment.type().id(), this.thirdAugment.type().id());
    }

    public enum Position
    {
        FIRST, SECOND, THIRD;
    }

    public static Augments get(ItemStack stack)
    {
        return stack.getOrDefault(ModDataComponents.AUGMENTS.get(), Augments.EMPTY);
    }

    public static void set(ItemStack stack, Augments augments)
    {
        stack.set(ModDataComponents.AUGMENTS.get(), augments);
    }
}
