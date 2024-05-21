package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.ChallengeUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.EventType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

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
        return builder.group(BlockPredicate.CODEC.optionalFieldOf("target").forGetter(challenge -> {
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

    public InteractWithBlockChallenge(Optional<BlockPredicate> block, Optional<ItemPredicate> item, int count)
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
            ChallengeUtils.writeBlockPredicate(buf, challenge.block);
            ChallengeUtils.writeItemPredicate(buf, challenge.item);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public InteractWithBlockChallenge read(FriendlyByteBuf buf)
        {
            Optional<BlockPredicate> block = ChallengeUtils.readBlockPredicate(buf);
            Optional<ItemPredicate> item = ChallengeUtils.readItemPredicate(buf);
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
        protected Tracker(int maxCount, Optional<BlockPredicate> block, Optional<ItemPredicate> item)
        {
            super(maxCount, ProgressFormatters.MINED_X_OF_X);
            UnlockManager.instance().addEventListener(EventType.USED_ITEM_ON_BLOCK, (state, stack, player) -> {
                if(this.isComplete() || player.level().isClientSide())
                    return;
                if(ChallengeUtils.testPredicate(block, state)) {
                    if(ChallengeUtils.testPredicate(item, stack)) {
                        this.increment(player);
                    }
                }
            });
        }
    }
}
