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
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class InteractWithEntityChallenge extends Challenge
{
    public static final ChallengeSerializer<InteractWithEntityChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "interact_with_entity"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(EntityPredicate.CODEC.optionalFieldOf("entity").forGetter(challenge -> {
                return challenge.entity;
            }), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(challenge -> {
                return challenge.item;
            }), EntityPredicate.CODEC.optionalFieldOf("player").forGetter(challenge -> {
                return challenge.player;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(challenge -> {
                return challenge.count;
            })).apply(builder, InteractWithEntityChallenge::new);
        })
    );

    private final Optional<EntityPredicate> entity;
    private final Optional<ItemPredicate> item;
    private final Optional<EntityPredicate> player;
    private final int count;

    public InteractWithEntityChallenge(Optional<EntityPredicate> entity, Optional<ItemPredicate> item, Optional<EntityPredicate> player, int count)
    {
        this.entity = entity;
        this.item = item;
        this.player = player;
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
        return new Tracker(backpackId, this.count, formatter, this.entity, this.item, this.player);
    }

    public static class Tracker extends CountProgressTracker
    {
        private final ResourceLocation backpackId;
        private final Optional<EntityPredicate> entity;
        private final Optional<ItemPredicate> item;
        private final Optional<EntityPredicate> player;

        private Tracker(ResourceLocation backpackId, int maxCount, ProgressFormatter formatter, Optional<EntityPredicate> entity, Optional<ItemPredicate> item, Optional<EntityPredicate> player)
        {
            super(maxCount, formatter);
            this.backpackId = backpackId;
            this.entity = entity;
            this.item = item;
            this.player = player;
        }

        private boolean test(ServerPlayer player, Entity entity, ItemStack stack)
        {
            return PredicateUtils.match(this.entity, player, entity) && PredicateUtils.match(this.item, stack) && PredicateUtils.match(this.player, player);
        }

        public static void registerEvent()
        {
            // We want to test the entity before the interaction.
            BackpackedEvents.INTERACTED_WITH_ENTITY_CAPTURE.register((player, stack, entity, consumer) -> {
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete() && tracker.test(player, entity, stack)) {
                        consumer.accept(tracker.backpackId);
                    }
                });
            });

            BackpackedEvents.INTERACTED_WITH_ENTITY.register((player, stack, entity, callbacks) -> {
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    // We don't need to test the predicates again
                    if(!tracker.isComplete() && callbacks.contains(tracker.backpackId)) {
                        tracker.increment(player);
                    }
                });
            });
        }
    }
}
