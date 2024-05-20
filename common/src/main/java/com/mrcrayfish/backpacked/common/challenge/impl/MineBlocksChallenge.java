package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.EventType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class MineBlocksChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "mine_blocks");
    public static final Serializer SERIALIZER = new Serializer();
    private static final Codec<List<Value>> VALUE_CODEC = ExtraCodecs.validate(Value.CODEC.listOf(), values -> {
        return !values.isEmpty() ? DataResult.success(values) : DataResult.error(() -> "You must specify at least one block or block tag");
    });
    public static final Codec<MineBlocksChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(VALUE_CODEC.fieldOf("blocks").forGetter(challenge -> {
            return challenge.values;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, MineBlocksChallenge::new);
    });

    private final List<Value> values;
    private final int count;

    private MineBlocksChallenge(List<Value> values, int count)
    {
        super(ID);
        this.values = values;
        this.count = count;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker()
    {
        return new Tracker(this.count, this.values);
    }

    protected interface Value
    {
        Codec<Value> CODEC = Codec.STRING.xmap(value -> {
            if(value.startsWith("#")) {
                ResourceLocation id = new ResourceLocation(value.substring(1));
                TagKey<Block> tag = TagKey.create(Registries.BLOCK, id);
                return new TagValue(tag);
            } else {
                ResourceLocation id = new ResourceLocation(value);
                Block block = BuiltInRegistries.BLOCK.get(id);
                return new BlockValue(block);
            }
        }, value -> {
            if(value instanceof BlockValue v) {
                return BuiltInRegistries.BLOCK.getKey(v.block).toString();
            } else if(value instanceof TagValue v) {
                return "#" + v.tag.location();
            }
            throw new RuntimeException("Something really messed up!");
        });

        static void write(Value value, FriendlyByteBuf buf)
        {
            if(value instanceof BlockValue v)
            {
                buf.writeVarInt(0);
                buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(v.block));
                return;
            }
            else if(value instanceof TagValue v)
            {
                buf.writeVarInt(1);
                buf.writeResourceLocation(v.tag.location());
                return;
            }
            throw new IllegalStateException("Invalid value!");
        }

        static Value read(FriendlyByteBuf buf)
        {
            int type = buf.readVarInt();
            return switch(type) {
                case 0 -> {
                    ResourceLocation id = buf.readResourceLocation();
                    Block block = BuiltInRegistries.BLOCK.get(id);
                    yield new BlockValue(block);
                }
                case 1 -> {
                    ResourceLocation id = buf.readResourceLocation();
                    TagKey<Block> tag = TagKey.create(Registries.BLOCK, id);
                    yield new TagValue(tag);
                }
                default -> throw new RuntimeException("Failed to read value for mine blocks challenge with type: " + type);
            };
        }

        boolean test(BlockState state);
    }

    private record BlockValue(Block block) implements Value
    {
        public static final Codec<BlockValue> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(value -> {
                return value.block;
            })).apply(builder, BlockValue::new);
        });

        @Override
        public boolean test(BlockState state)
        {
            return state.is(this.block);
        }
    }

    private record TagValue(TagKey<Block> tag) implements Value
    {
        public static final Codec<TagValue> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(value -> {
                return value.tag;
            })).apply(builder, TagValue::new);
        });

        @Override
        public boolean test(BlockState state)
        {
            return state.is(this.tag);
        }
    }

    public static class Serializer extends ChallengeSerializer<MineBlocksChallenge>
    {
        @Override
        public void write(MineBlocksChallenge challenge, FriendlyByteBuf buf)
        {
            buf.writeCollection(challenge.values, (buf1, value) -> Value.write(value, buf1));
            buf.writeVarInt(challenge.count);
        }

        @Override
        public MineBlocksChallenge read(FriendlyByteBuf buf)
        {
            List<Value> values = buf.readList(Value::read);
            int count = buf.readVarInt();
            return new MineBlocksChallenge(values, count);
        }

        @Override
        public Codec<MineBlocksChallenge> codec()
        {
            return MineBlocksChallenge.CODEC;
        }
    }

    protected static class Tracker extends CountProgressTracker
    {
        protected Tracker(int maxCount, List<Value> values)
        {
            super(maxCount, ProgressFormatters.MINED_X_OF_X);
            UnlockManager.instance().addEventListener(EventType.MINED_BLOCK, (state, player) -> {
                if(this.isComplete() || player.level().isClientSide())
                    return;
                if(values.stream().anyMatch(value -> value.test(state))) {
                    this.increment((ServerPlayer) player);
                }
            });
        }
    }
}
