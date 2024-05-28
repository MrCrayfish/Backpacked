package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.challenge.ChallengeUtils;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.FriendlyByteBuf;
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
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "merchant_trade");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<MerchantTradeChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(ProgressFormatter.CODEC.fieldOf("formatter").orElse(ProgressFormatter.TRADED_X_OF_X).forGetter(challenge -> {
            return challenge.formatter;
        }), ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "merchant").forGetter(o -> {
            return o.entity;
        }), ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(o -> {
            return o.item;
        }), ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(o -> {
            return o.count;
        })).apply(builder, MerchantTradeChallenge::new);
    });

    private final ProgressFormatter formatter;
    private final Optional<EntityPredicate> entity;
    private final Optional<ItemPredicate> item;
    private final int count;

    protected MerchantTradeChallenge(ProgressFormatter formatter, Optional<EntityPredicate> entity, Optional<ItemPredicate> item, int count)
    {
        super(ID);
        this.formatter = formatter;
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
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return new Tracker(this.count, this.formatter, this.entity, this.item);
    }

    public static class Serializer extends ChallengeSerializer<MerchantTradeChallenge>
    {
        @Override
        public void write(MerchantTradeChallenge challenge, FriendlyByteBuf buf)
        {
            ChallengeUtils.writeEntityPredicate(buf, challenge.entity);
            ChallengeUtils.writeItemPredicate(buf, challenge.item);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public MerchantTradeChallenge read(FriendlyByteBuf buf)
        {
            Optional<EntityPredicate> entity = ChallengeUtils.readEntityPredicate(buf);
            Optional<ItemPredicate> item = ChallengeUtils.readItemPredicate(buf);
            int count = buf.readVarInt();
            return new MerchantTradeChallenge(ProgressFormatter.TRADED_X_OF_X, entity, item, count);
        }

        @Override
        public Codec<MerchantTradeChallenge> codec()
        {
            return MerchantTradeChallenge.CODEC;
        }
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
            return this.entity.map(p -> p.matches(player, merchant)).orElse(true) && this.item.map(p -> p.matches(stack)).orElse(true);
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
