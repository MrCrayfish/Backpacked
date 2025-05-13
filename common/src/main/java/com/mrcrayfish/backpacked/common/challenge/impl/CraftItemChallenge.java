package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.predicates.CraftedItemPredicate;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CraftItemChallenge extends Challenge
{
    public static final ChallengeSerializer<CraftItemChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "craft_item"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(CraftedItemPredicate.CODEC.optionalFieldOf("crafted_item").forGetter(challenge -> {
                return challenge.predicate;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(challenge -> {
                return challenge.count;
            })).apply(builder, CraftItemChallenge::new);
        })
    );

    private final Optional<CraftedItemPredicate> predicate;
    private final int count;

    public CraftItemChallenge(Optional<CraftedItemPredicate> predicate, int count)
    {
        super();
        this.predicate = predicate;
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
        return new Tracker(formatter, this.predicate, this.count);
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<CraftedItemPredicate> predicate;

        public Tracker(ProgressFormatter formatter, Optional<CraftedItemPredicate> predicate, int maxCount)
        {
            super(maxCount, formatter);
            this.predicate = predicate;
        }

        public static void registerEvent()
        {
            PlayerEvents.CRAFT_ITEM.register((player, stack, inventory) -> {
                if(player.level().isClientSide())
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete())
                        return;
                    if(tracker.predicate.map(p -> p.test(stack)).orElse(true)) {
                        tracker.increment(stack.getCount(), (ServerPlayer) player);
                    }
                });
            });
        }
    }

}
