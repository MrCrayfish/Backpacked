package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.PredicateUtils;
import com.mrcrayfish.backpacked.common.predicates.BlockSnapshotPredicate;
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

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MineBlockChallenge extends Challenge
{
    public static final ChallengeSerializer<MineBlockChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "mine_block"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(BlockSnapshotPredicate.CODEC.optionalFieldOf("mined_block").forGetter(challenge -> {
                return challenge.block;
            }), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(challenge -> {
                return challenge.item;
            }), EntityPredicate.CODEC.optionalFieldOf("player").forGetter(challenge -> {
                return challenge.entity;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(challenge -> {
                return challenge.count;
            })).apply(builder, MineBlockChallenge::new);
        })
    );

    private final Optional<BlockSnapshotPredicate> block;
    private final Optional<ItemPredicate> item;
    private final Optional<EntityPredicate> entity;
    private final int count;

    public MineBlockChallenge(Optional<BlockSnapshotPredicate> block, Optional<ItemPredicate> item, Optional<EntityPredicate> entity, int count)
    {
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
        private final Optional<BlockSnapshotPredicate> block;
        private final Optional<ItemPredicate> item;
        private final Optional<EntityPredicate> entity;

        protected Tracker(int maxCount, ProgressFormatter formatter, Optional<BlockSnapshotPredicate> block, Optional<ItemPredicate> item, Optional<EntityPredicate> entity)
        {
            super(maxCount, formatter);
            this.block = block;
            this.item = item;
            this.entity = entity;
        }

        private boolean test(BlockSnapshot snapshot, ItemStack stack, ServerPlayer player)
        {
            return PredicateUtils.match(this.block, snapshot) && PredicateUtils.match(this.item, stack) && PredicateUtils.match(this.entity, player);
        }

        public static void registerEvent()
        {
            // Determines if we need to capture block entity compound tag for any tests
            BackpackedEvents.MINED_BLOCK_CAPTURE_TAG.register(player -> {
                if(player.level().isClientSide())
                    return false;
                return UnlockManager.getTrackers(player, Tracker.class).stream().anyMatch(tracker -> {
                    if(tracker.isComplete())
                        return false;
                    if(tracker.block.isPresent()) {
                        // Only capture tag if block nbt predicate is present
                        Optional<BlockPredicate> block = tracker.block.get().block();
                        return block.isPresent() && block.get().nbt().isPresent();
                    }
                    return false;
                });
            });

            // If this event is called, we have successfully mined a block and now we do tests
            BackpackedEvents.MINED_BLOCK.register((snapshot, stack, player) -> {
                if(player.level().isClientSide())
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete()) {
                        if(tracker.test(snapshot, stack, player)) {
                            tracker.increment(player);
                        }
                    }
                });
            });
        }
    }
}
