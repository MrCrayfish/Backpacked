package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.ChallengeUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.network.FriendlyByteBuf;
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
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "feed_animal");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<FeedAnimalChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(EntityPredicate.CODEC.optionalFieldOf("animal").forGetter(challenge -> {
            return challenge.entity;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, FeedAnimalChallenge::new);
    });

    private final Optional<EntityPredicate> entity;
    private final int count;

    public FeedAnimalChallenge(Optional<EntityPredicate> entity, int count)
    {
        super(ID);
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
        return new Tracker(this.count, this.entity);
    }

    public static class Serializer extends ChallengeSerializer<FeedAnimalChallenge>
    {
        @Override
        public void write(FeedAnimalChallenge challenge, FriendlyByteBuf buf)
        {
            ChallengeUtils.writeEntityPredicate(buf, challenge.entity);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public FeedAnimalChallenge read(FriendlyByteBuf buf)
        {
            Optional<EntityPredicate> entity = ChallengeUtils.readEntityPredicate(buf);
            int count = buf.readVarInt();
            return new FeedAnimalChallenge(entity, count);
        }

        @Override
        public Codec<FeedAnimalChallenge> codec()
        {
            return FeedAnimalChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EntityPredicate> entityPredicate;

        private Tracker(int maxCount, Optional<EntityPredicate> entityPredicate)
        {
            super(maxCount, ProgressFormatters.FED_X_OF_X);
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
