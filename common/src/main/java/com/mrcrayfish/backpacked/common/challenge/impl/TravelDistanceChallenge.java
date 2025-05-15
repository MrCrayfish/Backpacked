package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.MovementType;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.PredicateUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TravelDistanceChallenge extends Challenge
{
    public static final ChallengeSerializer<TravelDistanceChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "travel_distance"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(MovementType.LIST_CODEC.xmap(EnumSet::copyOf, List::copyOf).optionalFieldOf("movement").forGetter(challenge -> {
                return challenge.movementTypes;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("total_distance").forGetter(challenge -> {
                return challenge.totalDistanceInCm;
            }), EntityPredicate.CODEC.optionalFieldOf("player").forGetter(challenge -> {
                return challenge.player;
            })).apply(builder, TravelDistanceChallenge::new);
        })
    );

    private final Optional<EnumSet<MovementType>> movementTypes;
    private final int totalDistanceInCm;
    private final Optional<EntityPredicate> player;

    protected TravelDistanceChallenge(Optional<EnumSet<MovementType>> movementTypes, int totalDistanceInCm, Optional<EntityPredicate> player)
    {
        this.movementTypes = movementTypes;
        this.totalDistanceInCm = totalDistanceInCm;
        this.player = player;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return new Tracker(formatter, this.movementTypes, this.totalDistanceInCm, this.player);
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EnumSet<MovementType>> movementTypes;
        private final Optional<EntityPredicate> player;

        public Tracker(ProgressFormatter formatter, Optional<EnumSet<MovementType>> movementTypes, int maxCount, Optional<EntityPredicate> player)
        {
            super(maxCount, formatter);
            this.movementTypes = movementTypes;
            this.player = player;
        }

        public static void registerEvent()
        {
            BackpackedEvents.PLAYER_TRAVEL.register((player, distance, type) -> {
                int distanceInCm = Math.round((float) Math.sqrt(distance) * 100);
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete())
                        return;
                    if(tracker.movementTypes.map(types -> types.contains(type)).orElse(true)) {
                        if(PredicateUtils.match(tracker.player, player)) {
                            tracker.increment(distanceInCm, player);
                        }
                    }
                });
            });
        }
    }
}
