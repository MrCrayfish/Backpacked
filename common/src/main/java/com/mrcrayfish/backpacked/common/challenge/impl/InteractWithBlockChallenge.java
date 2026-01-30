package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonObject;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.PredicateUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.mixin.common.BlockPredicateAccessor;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class InteractWithBlockChallenge extends Challenge // TODO DONE
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "interact_with_block");
    public static final Serializer SERIALIZER = new Serializer();

    private final Optional<BlockPredicate> block;
    private final Optional<ItemPredicate> item;
    private final Optional<EntityPredicate> entity;
    private final int count;

    public InteractWithBlockChallenge(Optional<BlockPredicate> block, Optional<ItemPredicate> item, Optional<EntityPredicate> entity, int count)
    {
        super(ID);
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

    public static class Serializer extends ChallengeSerializer<InteractWithBlockChallenge>
    {
        @Override
        public InteractWithBlockChallenge deserialize(JsonObject object)
        {
            Optional<BlockPredicate> block = object.has("block") ? Optional.of(BlockPredicate.fromJson(object.get("block"))) : Optional.empty();
            Optional<ItemPredicate> item = object.has("item") ? Optional.of(ItemPredicate.fromJson(object.get("item"))) : Optional.empty();
            Optional<EntityPredicate> entity = object.has("player") ? Optional.of(EntityPredicate.fromJson(object.get("player"))) : Optional.empty();
            int count = readCount(object, 1);
            return new InteractWithBlockChallenge(block, item, entity, count);
        }
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

        private boolean needsTag()
        {
            return this.block.map(predicate -> ((BlockPredicateAccessor) predicate).backpacked$nbt() != NbtPredicate.ANY).orElse(false);
        }

        private boolean test(BlockSnapshot snapshot, ItemStack stack, ServerPlayer player)
        {
            return PredicateUtils.testPredicate(this.block, snapshot) && PredicateUtils.testPredicate(this.item, stack) && PredicateUtils.testPredicate(this.entity, player, player);
        }

        public static void registerEvent()
        {
            // Only capture the compound tag of the block entity if we need to
            BackpackedEvents.INTERACTED_WITH_BLOCK_CAPTURE_TAG.register(player -> {
                return UnlockManager.getIncompleteTrackers(player, Tracker.class).stream().anyMatch(tracker -> {
                    return !tracker.isComplete() && tracker.needsTag();
                });
            });

            // If this event is called, we have successfully interacted with block. Now update tracker
            BackpackedEvents.INTERACTED_WITH_BLOCK.register((snapshot, stack, player) -> {
                UnlockManager.getIncompleteTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete() && tracker.test(snapshot, stack, player)) {
                        tracker.increment(player);
                    }
                });
            });
        }
    }
}
