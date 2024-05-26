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
public class BreedAnimalChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "breed_animal");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<BreedAnimalChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(EntityPredicate.CODEC.optionalFieldOf("animal").forGetter(challenge -> {
            return challenge.entity;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, BreedAnimalChallenge::new);
    });

    private final Optional<EntityPredicate> entity;
    private final int count;

    protected BreedAnimalChallenge(Optional<EntityPredicate> entity, int count)
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
    public IProgressTracker createProgressTracker()
    {
        return new Tracker(this.count, this.entity);
    }

    public static class Serializer extends ChallengeSerializer<BreedAnimalChallenge>
    {
        @Override
        public void write(BreedAnimalChallenge challenge, FriendlyByteBuf buf)
        {
            ChallengeUtils.writeEntityPredicate(buf, challenge.entity);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public BreedAnimalChallenge read(FriendlyByteBuf buf)
        {
            Optional<EntityPredicate> entity = ChallengeUtils.readEntityPredicate(buf);
            int count = buf.readVarInt();
            return new BreedAnimalChallenge(entity, count);
        }

        @Override
        public Codec<BreedAnimalChallenge> codec()
        {
            return BreedAnimalChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EntityPredicate> predicate;

        public Tracker(int maxCount, Optional<EntityPredicate> predicate)
        {
            super(maxCount, ProgressFormatters.BRED_X_OF_X);
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
