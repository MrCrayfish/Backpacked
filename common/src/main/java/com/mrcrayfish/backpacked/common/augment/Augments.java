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

public record Augments(Augment<?> firstAugment, boolean firstState, Augment<?> secondAugment, boolean secondState, Augment<?> thirdAugment, boolean thirdState, Augment<?> fourthAugment, boolean fourthState)
{
    public static final Augments EMPTY = new Augments(EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true, EmptyAugment.INSTANCE, true);

    public static final Codec<Augments> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Augment.CODEC.optionalFieldOf("first", EmptyAugment.INSTANCE).forGetter(Augments::firstAugment),
        Codec.BOOL.optionalFieldOf("firstState", true).forGetter(Augments::firstState),
        Augment.CODEC.optionalFieldOf("second", EmptyAugment.INSTANCE).forGetter(Augments::secondAugment),
        Codec.BOOL.optionalFieldOf("secondState", true).forGetter(Augments::secondState),
        Augment.CODEC.optionalFieldOf("third", EmptyAugment.INSTANCE).forGetter(Augments::thirdAugment),
        Codec.BOOL.optionalFieldOf("thirdState", true).forGetter(Augments::thirdState),
        Augment.CODEC.optionalFieldOf("fourth", EmptyAugment.INSTANCE).forGetter(Augments::fourthAugment),
        Codec.BOOL.optionalFieldOf("fourthState", true).forGetter(Augments::fourthState)
    ).apply(builder, Augments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Augments> STREAM_CODEC = StreamCodec.of((buf, augments) -> {
        Augment.STREAM_CODEC.encode(buf, augments.firstAugment);
        buf.writeBoolean(augments.firstState);
        Augment.STREAM_CODEC.encode(buf, augments.secondAugment);
        buf.writeBoolean(augments.secondState);
        Augment.STREAM_CODEC.encode(buf, augments.thirdAugment);
        buf.writeBoolean(augments.thirdState);
        Augment.STREAM_CODEC.encode(buf, augments.fourthAugment);
        buf.writeBoolean(augments.fourthState);
    }, buf -> {
        Augment<?> firstAugment = Augment.STREAM_CODEC.decode(buf);
        boolean firstState = buf.readBoolean();
        Augment<?> secondAugment = Augment.STREAM_CODEC.decode(buf);
        boolean secondState = buf.readBoolean();
        Augment<?> thirdAugment = Augment.STREAM_CODEC.decode(buf);
        boolean thirdState = buf.readBoolean();
        Augment<?> fourthAugment = Augment.STREAM_CODEC.decode(buf);
        boolean fourthState = buf.readBoolean();
        return new Augments(firstAugment, firstState, secondAugment, secondState, thirdAugment, thirdState, fourthAugment, fourthState);
    });

    public Augment<?> getAugment(Position position)
    {
        return switch(position) {
            case FIRST -> restrict(this.firstAugment);
            case SECOND -> restrict(this.secondAugment);
            case THIRD -> restrict(this.thirdAugment);
            case FOURTH -> restrict(this.fourthAugment);
        };
    }

    public Augments setAugment(Position position, Augment<?> augment)
    {
        return switch(position) {
            case FIRST -> new Augments(augment, this.firstState, this.secondAugment, this.secondState, this.thirdAugment, this.thirdState, this.fourthAugment, this.fourthState);
            case SECOND -> new Augments(this.firstAugment, this.firstState, augment, this.secondState, this.thirdAugment, this.thirdState, this.fourthAugment, this.fourthState);
            case THIRD -> new Augments(this.firstAugment, this.firstState, this.secondAugment, this.secondState, augment, this.thirdState, this.fourthAugment, this.fourthState);
            case FOURTH -> new Augments(this.firstAugment, this.firstState, this.secondAugment, this.secondState, this.thirdAugment, this.thirdState, augment, this.fourthState);
        };
    }

    public boolean getState(Position position)
    {
        return switch(position) {
            case FIRST -> this.firstState;
            case SECOND -> this.secondState;
            case THIRD -> this.thirdState;
            case FOURTH -> this.fourthState;
        };
    }

    public Augments setState(Position position, boolean state)
    {
        return switch(position) {
            case FIRST -> new Augments(this.firstAugment, state, this.secondAugment, this.secondState, this.thirdAugment, this.thirdState, this.fourthAugment, this.fourthState);
            case SECOND -> new Augments(this.firstAugment, this.firstState, this.secondAugment, state, this.thirdAugment, this.thirdState, this.fourthAugment, this.fourthState);
            case THIRD -> new Augments(this.firstAugment, this.firstState, this.secondAugment, this.secondState, this.thirdAugment, state, this.fourthAugment, this.fourthState);
            case FOURTH -> new Augments(this.firstAugment, this.firstState, this.secondAugment, this.secondState, this.thirdAugment, this.thirdState, this.fourthAugment, state);
        };
    }

    public <T extends Augment<T>> boolean has(AugmentType<T> type)
    {
        if(Config.getDisabledAugments().contains(type.id()))
            return false;
        return this.firstAugment.type() == type || this.secondAugment.type() == type || this.thirdAugment.type() == type;
    }

    public enum Position
    {
        FIRST, SECOND, THIRD, FOURTH;

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
