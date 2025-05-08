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
public class MerchantTradeChallenge extends Challenge
{
    public static final ChallengeSerializer<MerchantTradeChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "merchant_trade"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(EntityPredicate.CODEC.optionalFieldOf("merchant").forGetter(o -> {
                return o.entity;
            }), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(o -> {
                return o.item;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(o -> {
                return o.count;
            })).apply(builder, MerchantTradeChallenge::new);
        })
    );

    private final Optional<EntityPredicate> entity;
    private final Optional<ItemPredicate> item;
    private final int count;

    protected MerchantTradeChallenge(Optional<EntityPredicate> entity, Optional<ItemPredicate> item, int count)
    {
        super();
        this.entity = entity;
        this.item = item;
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
        return new Tracker(this.count, formatter, this.entity, this.item);
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EntityPredicate> entity;
        private final Optional<ItemPredicate> item;

        public Tracker(int maxCount, ProgressFormatter formatter, Optional<EntityPredicate> entity, Optional<ItemPredicate> item)
        {
            super(maxCount, formatter);
            this.entity = entity;
            this.item = item;
        }

        private boolean test(ServerPlayer player, Entity merchant, ItemStack stack)
        {
            return this.entity.map(p -> p.matches(player, merchant)).orElse(true) && this.item.map(p -> p.test(stack)).orElse(true);
        }

        public static void registerEvent()
        {
            BackpackedEvents.MERCHANT_TRADE.register((merchant, player, stack) -> {
                if(player.level().isClientSide() || !(merchant instanceof Entity entity))
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete())
                        return;
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    if(tracker.test(serverPlayer, entity, stack)) {
                        tracker.increment(serverPlayer);
                    }
                });
            });
        }
    }
}
