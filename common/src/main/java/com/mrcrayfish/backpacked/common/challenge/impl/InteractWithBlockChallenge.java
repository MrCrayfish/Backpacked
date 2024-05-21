package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.EventType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class InteractWithBlockChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "interact_with_block");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<InteractWithBlockChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(BlockPredicate.CODEC.optionalFieldOf("block").forGetter(challenge -> {
            return challenge.block;
        }), ItemPredicate.CODEC.optionalFieldOf("using_item").forGetter(challenge -> {
            return challenge.item;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, InteractWithBlockChallenge::new);
    });

    private final Optional<BlockPredicate> block;
    private final Optional<ItemPredicate> item;
    private final int count;

    protected InteractWithBlockChallenge(Optional<BlockPredicate> block, Optional<ItemPredicate> item, int count)
    {
        super(ID);
        this.block = block;
        this.item = item;
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
        return new Tracker(this.count, this.block, this.item);
    }

    public static class Serializer extends ChallengeSerializer<InteractWithBlockChallenge>
    {
        @Override
        public void write(InteractWithBlockChallenge challenge, FriendlyByteBuf buf)
        {
            buf.writeOptional(challenge.block, (buf1, predicate) -> {
                buf1.writeNbt(BlockPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                    .getOrThrow(false, Constants.LOG::error));
            });
            buf.writeOptional(challenge.item, (buf1, predicate) -> {
                buf1.writeNbt(ItemPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                    .getOrThrow(false, Constants.LOG::error));
            });
            buf.writeVarInt(challenge.count);
        }

        @Override
        public InteractWithBlockChallenge read(FriendlyByteBuf buf)
        {
            Optional<BlockPredicate> block = buf.readOptional(buf1 -> {
                return BlockPredicate.CODEC.parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
                    .getOrThrow(false, Constants.LOG::error);
            });
            Optional<ItemPredicate> item = buf.readOptional(buf1 -> {
                return ItemPredicate.CODEC.parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
                    .getOrThrow(false, Constants.LOG::error);
            });
            int count = buf.readVarInt();
            return new InteractWithBlockChallenge(block, item, count);
        }

        @Override
        public Codec<InteractWithBlockChallenge> codec()
        {
            return InteractWithBlockChallenge.CODEC;
        }
    }

    protected static class Tracker extends CountProgressTracker
    {
        private final Optional<BlockPredicate> blockPredicate;
        private final Optional<ItemPredicate> itemPredicate;

        protected Tracker(int maxCount, Optional<BlockPredicate> block, Optional<ItemPredicate> item)
        {
            super(maxCount, ProgressFormatters.MINED_X_OF_X);
            this.blockPredicate = block;
            this.itemPredicate = item;
            UnlockManager.instance().addEventListener(EventType.USED_ITEM_ON_BLOCK, (state, stack, player) -> {
                if(this.isComplete() || player.level().isClientSide())
                    return;
                if(this.testState(state) && this.testItem(stack)) {
                    this.increment(player);
                }
            });
        }

        private boolean testState(BlockState state)
        {
            if(this.blockPredicate.isEmpty())
                return true;
            BlockPredicate predicate = this.blockPredicate.get();
            if(predicate.tag().isPresent() && !state.is(predicate.tag().get()))
                return false;
            if(predicate.blocks().isPresent() && !state.is(predicate.blocks().get()))
                return false;
            return predicate.properties().isEmpty() || predicate.properties().get().matches(state);
        }

        private boolean testItem(ItemStack stack)
        {
            return this.itemPredicate.map(predicate -> predicate.matches(stack)).orElse(true);
        }
    }
}
