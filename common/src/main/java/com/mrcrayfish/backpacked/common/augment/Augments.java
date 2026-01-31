package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public sealed class Augments permits Augments.Cached
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

    protected Augment<?> firstAugment;
    protected Boolean firstState;
    protected Augment<?> secondAugment;
    protected Boolean secondState;
    protected Augment<?> thirdAugment;
    protected Boolean thirdState;
    protected Augment<?> fourthAugment;
    protected Boolean fourthState;

    private Augments() {}

    public Augments(Augment<?> firstAugment, Boolean firstState, Augment<?> secondAugment, Boolean secondState, Augment<?> thirdAugment, Boolean thirdState, Augment<?> fourthAugment, Boolean fourthState)
    {
        this.firstAugment = firstAugment;
        this.firstState = firstState;
        this.secondAugment = secondAugment;
        this.secondState = secondState;
        this.thirdAugment = thirdAugment;
        this.thirdState = thirdState;
        this.fourthAugment = fourthAugment;
        this.fourthState = fourthState;
    }

    public Augment<?> firstAugment()
    {
        return this.firstAugment;
    }

    public boolean firstState()
    {
        return this.firstState;
    }

    public Augment<?> secondAugment()
    {
        return this.secondAugment;
    }

    public boolean secondState()
    {
        return this.secondState;
    }

    public Augment<?> thirdAugment()
    {
        return this.thirdAugment;
    }

    public boolean thirdState()
    {
        return this.thirdState;
    }

    public Augment<?> fourthAugment()
    {
        return this.fourthAugment;
    }

    public boolean fourthState()
    {
        return this.fourthState;
    }

    public Augment<?> getAugment(Position position)
    {
        return switch(position)
        {
            case FIRST -> restrict(this.firstAugment());
            case SECOND -> restrict(this.secondAugment());
            case THIRD -> restrict(this.thirdAugment());
            case FOURTH -> restrict(this.fourthAugment());
        };
    }

    public void setAugment(Position position, Augment<?> augment)
    {
        switch(position)
        {
            case FIRST -> this.firstAugment = augment;
            case SECOND -> this.secondAugment = augment;
            case THIRD -> this.thirdAugment = augment;
            case FOURTH -> this.fourthAugment = augment;
        };
    }

    public boolean getState(Position position)
    {
        return switch(position)
        {
            case FIRST -> this.firstState;
            case SECOND -> this.secondState;
            case THIRD -> this.thirdState;
            case FOURTH -> this.fourthState;
        };
    }

    public void setState(Position position, boolean state)
    {
        switch(position)
        {
            case FIRST -> this.firstState = state;
            case SECOND -> this.secondState = state;
            case THIRD -> this.thirdState = state;
            case FOURTH -> this.fourthState = state;
        };
    }

    public <T extends Augment<T>> boolean has(AugmentType<T> type)
    {
        if(Config.getDisabledAugments().contains(type.id()))
            return false;
        return this.firstAugment.type() == type || this.secondAugment.type() == type || this.thirdAugment.type() == type;
    }

    public Augments copy()
    {
        return new Augments(this.firstAugment(), this.firstState(), this.secondAugment(), this.secondState(), this.thirdAugment(), this.thirdState(), this.fourthAugment(), this.fourthState());
    }

    public Optional<Position> findPosition(AugmentType<?> type)
    {
        for(Position position : Position.values())
        {
            if(this.getAugment(position).type() == type)
            {
                return Optional.of(position);
            }
        }
        return Optional.empty();
    }

    public enum Position
    {
        FIRST("first"), SECOND("second"), THIRD("third"), FOURTH("fourth");

        private final String key;

        Position(String key)
        {
            this.key = key;
        }

        public String key()
        {
            return this.key;
        }
    }

    public static Augment<?> restrict(Augment<?> augment)
    {
        if(Config.getDisabledAugments().contains(augment.type().id()))
        {
            return EmptyAugment.INSTANCE;
        }
        return augment;
    }

    public void encode(FriendlyByteBuf buf)
    {
        Augment.encode(buf, this.firstAugment());
        buf.writeBoolean(this.firstState());
        Augment.encode(buf, this.secondAugment());
        buf.writeBoolean(this.secondState());
        Augment.encode(buf, this.thirdAugment());
        buf.writeBoolean(this.thirdState());
        Augment.encode(buf, this.fourthAugment());
        buf.writeBoolean(this.fourthState());
    }

    public static Augments decode(FriendlyByteBuf buf)
    {
        Augment<?> firstAugment = Augment.decode(buf);
        boolean firstState = buf.readBoolean();
        Augment<?> secondAugment = Augment.decode(buf);
        boolean secondState = buf.readBoolean();
        Augment<?> thirdAugment = Augment.decode(buf);
        boolean thirdState = buf.readBoolean();
        Augment<?> fourthAugment = Augment.decode(buf);
        boolean fourthState = buf.readBoolean();
        return new Augments(firstAugment, firstState, secondAugment, secondState, thirdAugment, thirdState, fourthAugment, fourthState);
    }

    public static Augments cached(ItemStack stack)
    {
        return new Cached(stack);
    }

    protected static final class Cached extends Augments
    {
        private static final String KEY = "BackpackedAugments";

        private final ItemStack stack;

        private Cached(ItemStack stack)
        {
            this.stack = stack;
        }

        @Override
        public <T extends Augment<T>> boolean has(AugmentType<T> type)
        {
            if(Config.getDisabledAugments().contains(type.id()))
                return false;

            CompoundTag tag = this.stack.getOrCreateTag();
            if(!tag.contains(KEY, Tag.TAG_COMPOUND))
                return false;

            CompoundTag augments = tag.getCompound(KEY);
            for(Position position : Position.values())
            {
                if(!augments.contains(position.key(), Tag.TAG_COMPOUND))
                    continue;

                CompoundTag augmentTag = augments.getCompound(position.key());
                if(!augmentTag.contains("Value", Tag.TAG_COMPOUND))
                    continue;

                CompoundTag valueTag = augmentTag.getCompound("Value");
                if(type.id().toString().equals(valueTag.getString("type")))
                    return true;
            }
            return false;
        }

        private Augment<?> readAugment(Position position)
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(!tag.contains(KEY, Tag.TAG_COMPOUND))
                return EmptyAugment.INSTANCE;

            CompoundTag augments = tag.getCompound(KEY);
            if(!augments.contains(position.key(), Tag.TAG_COMPOUND))
                return EmptyAugment.INSTANCE;

            CompoundTag augment = augments.getCompound(position.key());
            if(!augment.contains("Value", Tag.TAG_COMPOUND))
                return EmptyAugment.INSTANCE;

            CompoundTag value = augment.getCompound("Value");
            return Augment.CODEC.parse(NbtOps.INSTANCE, value).result().orElse(EmptyAugment.INSTANCE);
        }

        private boolean readState(Position position)
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(!tag.contains(KEY, Tag.TAG_COMPOUND))
                return true;

            CompoundTag augments = tag.getCompound(KEY);
            if(!augments.contains(position.key(), Tag.TAG_COMPOUND))
                return true;

            CompoundTag augment = augments.getCompound(position.key());
            return augment.getBoolean("State");
        }

        @Override
        public Augment<?> firstAugment()
        {
            if(this.firstAugment == null)
            {
                this.firstAugment = this.readAugment(Position.FIRST);
            }
            return this.firstAugment;
        }

        @Override
        public boolean firstState()
        {
            if(this.firstState == null)
            {
                this.firstState = this.readState(Position.FIRST);
            }
            return this.firstState;
        }

        @Override
        public Augment<?> secondAugment()
        {
            if(this.secondAugment == null)
            {
                this.secondAugment = this.readAugment(Position.SECOND);
            }
            return this.secondAugment;
        }

        @Override
        public boolean secondState()
        {
            if(this.secondState == null)
            {
                this.secondState = this.readState(Position.SECOND);
            }
            return this.secondState;
        }

        @Override
        public Augment<?> thirdAugment()
        {
            if(this.thirdAugment == null)
            {
                this.thirdAugment = this.readAugment(Position.THIRD);
            }
            return this.thirdAugment;
        }

        @Override
        public boolean thirdState()
        {
            if(this.thirdState == null)
            {
                this.thirdState = this.readState(Position.THIRD);
            }
            return this.thirdState;
        }

        @Override
        public Augment<?> fourthAugment()
        {
            if(this.fourthAugment == null)
            {
                this.fourthAugment = this.readAugment(Position.FOURTH);
            }
            return this.fourthAugment;
        }

        @Override
        public boolean fourthState()
        {
            if(this.fourthState == null)
            {
                this.fourthState = this.readState(Position.FOURTH);
            }
            return this.fourthState;
        }

        @Override
        public void setAugment(Position position, Augment<?> augment)
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            CompoundTag augmentsTag = tag.getCompound(KEY);
            CompoundTag augmentTag = augmentsTag.getCompound(position.key());
            Augment.CODEC.encodeStart(NbtOps.INSTANCE, augment).resultOrPartial(Constants.LOG::error).ifPresent(tag1 -> {
                augmentTag.put("Value", tag1);
            });
            augmentsTag.put(position.key(), augmentTag);
            tag.put(KEY, augmentsTag);
            super.setAugment(position, augment);
        }

        @Override
        public void setState(Position position, boolean state)
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            CompoundTag augmentsTag = tag.getCompound(KEY);
            CompoundTag augmentTag = augmentsTag.getCompound(position.key());
            augmentTag.putBoolean("State", state);
            augmentsTag.put(position.key(), augmentTag);
            tag.put(KEY, augmentsTag);
            super.setState(position, state);
        }

        @Override
        public Optional<Position> findPosition(AugmentType<?> type)
        {
            CompoundTag tag = this.stack.getTag();
            if(tag == null || !tag.contains(KEY, Tag.TAG_COMPOUND))
                return Optional.empty();

            CompoundTag augments = tag.getCompound(KEY);
            for(Position position : Position.values())
            {
                if(!augments.contains(position.key(), Tag.TAG_COMPOUND))
                    continue;

                CompoundTag augment = augments.getCompound(position.key());
                if(!augment.contains("Value", Tag.TAG_COMPOUND))
                    continue;

                CompoundTag value = augment.getCompound("Value");
                if(value.getString("type").equals(type.name().toString()))
                {
                    return Optional.of(position);
                }
            }
            return Optional.empty();
        }
    }
}
