package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.ChallengeUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
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

    private final ProgressFormatter formatter;
    private final Optional<BlockPredicate> block;
    private final Optional<ItemPredicate> item;
    private final int count;

    public MineBlockChallenge(ProgressFormatter formatter, Optional<BlockPredicate> block, Optional<ItemPredicate> item, int count)
    {
        super(ID);
        this.formatter = formatter;
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
        return new Tracker(this.count, this.formatter, this.block, this.item);
    }

    public static class Serializer extends ChallengeSerializer<MineBlockChallenge>
    {
        @Override
        public MineBlockChallenge deserialize(JsonObject object)
        {
            ProgressFormatter formatter = readFormatter(object, ProgressFormatter.MINED_X_OF_X);
            Optional<BlockPredicate> block = object.has("block") ? Optional.of(BlockPredicate.fromJson(object.get("block"))) : Optional.empty();
            Optional<ItemPredicate> item = object.has("item") ? Optional.of(ItemPredicate.fromJson(object.get("item"))) : Optional.empty();
            int count = readCount(object, 1);
            return new MineBlockChallenge(formatter, block, item, count);
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<BlockPredicate> block;
        private final Optional<ItemPredicate> item;

        protected Tracker(int maxCount, ProgressFormatter formatter, Optional<BlockPredicate> block, Optional<ItemPredicate> item)
        {
            super(maxCount, formatter);
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
