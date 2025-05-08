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
public class FeedAnimalChallenge extends Challenge
{
    public static final ChallengeSerializer<FeedAnimalChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "feed_animal"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(EntityPredicate.CODEC.optionalFieldOf("animal").forGetter(challenge -> {
                return challenge.entity;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(challenge -> {
                return challenge.count;
            })).apply(builder, FeedAnimalChallenge::new);
        })
    );

    private final Optional<EntityPredicate> entity;
    private final int count;

    public FeedAnimalChallenge(Optional<EntityPredicate> entity, int count)
    {
        super();
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
        private final Optional<EntityPredicate> entityPredicate;

        private Tracker(int maxCount, ProgressFormatter formatter, Optional<EntityPredicate> entityPredicate)
        {
            super(maxCount, formatter);
            this.entityPredicate = entityPredicate;
        }

        private boolean test(ServerPlayer player, Animal animal)
        {
            return this.entityPredicate.map(p -> p.matches(player, animal)).orElse(true);
        }

        public static void registerEvent()
        {
            BackpackedEvents.FEED_ANIMAL.register((animal, player) -> {
                if(player.level().isClientSide())
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    if(!tracker.isComplete() && tracker.test(serverPlayer, animal)) {
                        tracker.increment(serverPlayer);
                    }
                });
            });
        }
    }
}
