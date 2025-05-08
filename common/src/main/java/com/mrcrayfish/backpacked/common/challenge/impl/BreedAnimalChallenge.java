package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.animal.Animal;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BreedAnimalChallenge extends Challenge
{
    public static final ChallengeSerializer<BreedAnimalChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "breed_animal"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(EntityPredicate.CODEC.optionalFieldOf("animal").forGetter(challenge -> {
                return challenge.entity;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(challenge -> {
                return challenge.count;
            })).apply(builder, BreedAnimalChallenge::new);
        })
    );

    private final Optional<EntityPredicate> entity;
    private final int count;

    protected BreedAnimalChallenge(Optional<EntityPredicate> entity, int count)
    {
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
        return new Tracker(this.count, formatter, this.entity);
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EntityPredicate> predicate;

        public Tracker(int maxCount, ProgressFormatter formatter, Optional<EntityPredicate> predicate)
        {
            super(maxCount, formatter);
            this.predicate = predicate;
        }

        private boolean test(ServerPlayer player, Animal first, Animal second)
        {
            return this.predicate.map(p -> p.matches(player, first)).orElse(true) && this.predicate.map(p -> p.matches(player, second)).orElse(true);
        }

        public static void registerEvent()
        {
            BackpackedEvents.BRED_ANIMAL.register((first, second, player) -> {
                UnlockManager.getTrackers(player, BreedAnimalChallenge.Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete() && tracker.test(player, first, second)) {
                        tracker.increment(player);
                    }
                });
            });
        }
    }
}
