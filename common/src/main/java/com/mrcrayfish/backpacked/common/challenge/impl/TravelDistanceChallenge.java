package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.MovementType;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TravelDistanceChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "travel_distance");
    public static final Serializer SERIALIZER = new Serializer();

    private final Optional<EnumSet<MovementType>> movementTypes;
    private final int totalDistanceInCm;

    protected TravelDistanceChallenge(Optional<EnumSet<MovementType>> movementTypes, int totalDistanceInCm)
    {
        super(ID);
        this.movementTypes = movementTypes;
        this.totalDistanceInCm = totalDistanceInCm;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return new Tracker(formatter, this.movementTypes, this.totalDistanceInCm);
    }

    public static class Serializer extends ChallengeSerializer<TravelDistanceChallenge>
    {
        @Override
        public TravelDistanceChallenge deserialize(JsonObject object)
        {
            Optional<EnumSet<MovementType>> movements = object.has("movement") ? MovementType.LIST_CODEC.xmap(EnumSet::copyOf, List::copyOf).parse(JsonOps.INSTANCE, object.get("movement")).result() : Optional.empty();
            int totalDistance = GsonHelper.getAsInt(object, "total_distance");
            if(totalDistance <= 0)
                throw new JsonParseException("Total distance must be greater than zero. Found " + totalDistance);
            return new TravelDistanceChallenge(movements, totalDistance);
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EnumSet<MovementType>> movementTypes;

        public Tracker(ProgressFormatter formatter, Optional<EnumSet<MovementType>> movementTypes, int maxCount)
        {
            super(maxCount, formatter);
            this.movementTypes = movementTypes;
        }

        public static void registerEvent()
        {
            BackpackedEvents.PLAYER_TRAVEL.register((player, distance, type) -> {
                int distanceInCm = Math.round((float) Math.sqrt(distance) * 100);
                UnlockManager.getIncompleteTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete())
                        return;
                    if(tracker.movementTypes.map(types -> types.contains(type)).orElse(true)) {
                        tracker.increment(distanceInCm, player);
                    }
                });
            });
        }
    }
}
