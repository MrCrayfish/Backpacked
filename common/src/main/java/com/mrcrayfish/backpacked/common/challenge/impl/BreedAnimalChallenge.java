package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonObject;
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
import net.minecraft.world.entity.animal.Animal;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BreedAnimalChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "breed_animal");
    public static final Serializer SERIALIZER = new Serializer();

    private final ProgressFormatter formatter;
    private final Optional<EntityPredicate> entity;
    private final int count;

    protected BreedAnimalChallenge(ProgressFormatter formatter, Optional<EntityPredicate> entity, int count)
    {
        super(ID);
        this.formatter = formatter;
        this.entity = entity;
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
        return new Tracker(this.count, this.formatter, this.entity);
    }

    public static class Serializer extends ChallengeSerializer<BreedAnimalChallenge>
    {
        @Override
        public BreedAnimalChallenge deserialize(JsonObject object)
        {
            ProgressFormatter formatter = readFormatter(object, ProgressFormatter.BRED_X_OF_X);
            Optional<EntityPredicate> predicate = object.has("animal") ? Optional.of(EntityPredicate.fromJson(object.get("animal"))) : Optional.empty();
            int count = readCount(object, 1);
            return new BreedAnimalChallenge(formatter, predicate, count);
        }
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
