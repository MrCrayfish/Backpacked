package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.PredicateUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
    public static final ChallengeSerializer<InteractWithBlockChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "interact_with_block"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(BlockPredicate.CODEC.optionalFieldOf("block").forGetter(challenge -> {
                return challenge.block;
            }), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(challenge -> {
                return challenge.item;
            }), EntityPredicate.CODEC.optionalFieldOf("player").forGetter(challenge -> {
                return challenge.entity;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(challenge -> {
                return challenge.count;
            })).apply(builder, InteractWithBlockChallenge::new);
        })
    );

    private final Optional<BlockPredicate> block;
    private final Optional<ItemPredicate> item;
    private final Optional<EntityPredicate> entity;
    private final int count;

    public InteractWithBlockChallenge(Optional<BlockPredicate> block, Optional<ItemPredicate> item, Optional<EntityPredicate> entity, int count)
    {
        super();
        this.block = block;
        this.item = item;
        this.entity = entity;
        this.count = count;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return new Tracker(this.count, formatter, this.block, this.item, this.entity);
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<BlockPredicate> block;
        private final Optional<ItemPredicate> item;
        private final Optional<EntityPredicate> entity;

        private Tracker(int maxCount, ProgressFormatter formatter, Optional<BlockPredicate> block, Optional<ItemPredicate> item, Optional<EntityPredicate> entity)
        {
            super(maxCount, formatter);
            this.block = block;
            this.item = item;
            this.entity = entity;
        }

        private boolean test(BlockState state, ItemStack stack, ServerPlayer player)
        {
            return PredicateUtils.match(this.block, state, null) && PredicateUtils.match(this.item, stack) && PredicateUtils.match(this.entity, player);
        }

        public static void registerEvent()
        {
            // Only capture the compound tag of the block entity if we need to
            BackpackedEvents.INTERACTED_WITH_BLOCK_CAPTURE_TAG.register((state, stack, player) -> {
                return UnlockManager.getTrackers(player, Tracker.class).stream().anyMatch(tracker -> {
                    return !tracker.isComplete() && tracker.test(state, stack, player);
                });
            });

            // If this event is called, we have successfully interacted with block. Now update tracker
            BackpackedEvents.INTERACTED_WITH_BLOCK.register((state, stack, tag, player) -> {
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete() && tracker.test(state, stack, player)) {
                        tracker.increment(player);
                    }
                });
            });
        }
    }
}
