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
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MineBlockChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "mine_block");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<MineBlockChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(BlockPredicate.CODEC.optionalFieldOf("block").forGetter(challenge -> {
            return challenge.block;
        }), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(challenge -> {
            return challenge.item;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, MineBlockChallenge::new);
    });

    private final Optional<BlockPredicate> block;
    private final Optional<ItemPredicate> item;
    private final int count;

    public MineBlockChallenge(Optional<BlockPredicate> block, Optional<ItemPredicate> item, int count)
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
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return new Tracker(this.count, this.block, this.item);
    }

    public static class Serializer extends ChallengeSerializer<MineBlockChallenge>
    {
        @Override
        public void write(MineBlockChallenge challenge, FriendlyByteBuf buf)
        {
            ChallengeUtils.writeBlockPredicate(buf, challenge.block);
            ChallengeUtils.writeItemPredicate(buf, challenge.item);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public MineBlockChallenge read(FriendlyByteBuf buf)
        {
            Optional<BlockPredicate> block = ChallengeUtils.readBlockPredicate(buf);
            Optional<ItemPredicate> item = ChallengeUtils.readItemPredicate(buf);
            int count = buf.readVarInt();
            return new MineBlockChallenge(block, item, count);
        }

        @Override
        public Codec<MineBlockChallenge> codec()
        {
            return MineBlockChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<BlockPredicate> block;
        private final Optional<ItemPredicate> item;

        protected Tracker(int maxCount, Optional<BlockPredicate> block, Optional<ItemPredicate> item)
        {
            super(maxCount, ProgressFormatters.MINED_X_OF_X);
            this.block = block;
            this.item = item;
        }

        private boolean test(BlockState state, ItemStack stack, @Nullable CompoundTag tag)
        {
            return ChallengeUtils.testPredicate(this.block, state, tag) && ChallengeUtils.testPredicate(this.item, stack);
        }

        public static void registerEvent()
        {
            // Determines if we need to capture block entity compound tag for any tests
            BackpackedEvents.MINED_BLOCK_CAPTURE_TAG.register((state, stack, player) -> {
                if(player.level().isClientSide())
                    return false;
                return UnlockManager.getTrackers(player, Tracker.class).stream().anyMatch(tracker -> {
                    return !tracker.isComplete() && tracker.test(state, stack, null);
                });
            });

            // If this event is called, we have successfully mined a block and now we do tests
            BackpackedEvents.MINED_BLOCK.register((state, stack, tag, player) -> {
                if(player.level().isClientSide())
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete() && tracker.test(state, stack, tag)) {
                        tracker.increment((ServerPlayer) player);
                    }
                });
            });
        }
    }
}
