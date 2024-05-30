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
import com.mrcrayfish.framework.api.event.EntityEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class KillMobChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "kill_mob");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<KillMobChallenge> CODEC = RecordCodecBuilder.create(builder -> {
       return builder.group(ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "mob").forGetter(challenge -> {
           return challenge.entity;
       }), ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(challenge -> {
           return challenge.item;
       }), ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(challenge -> {
           return challenge.count;
       })).apply(builder, KillMobChallenge::new);
    });

    private final Optional<EntityPredicate> entity;
    private final Optional<ItemPredicate> item;
    private final int count;

    public KillMobChallenge(Optional<EntityPredicate> entity, Optional<ItemPredicate> item, int count)
    {
        super(ID);
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
        return new Tracker(this.count, this.entity, this.item);
    }

    public static final class Serializer extends ChallengeSerializer<KillMobChallenge>
    {
        @Override
        public void write(KillMobChallenge challenge, FriendlyByteBuf buf)
        {
            ChallengeUtils.writeEntityPredicate(buf, challenge.entity);
            ChallengeUtils.writeItemPredicate(buf, challenge.item);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public KillMobChallenge read(FriendlyByteBuf buf)
        {
            Optional<EntityPredicate> entity = ChallengeUtils.readEntityPredicate(buf);
            Optional<ItemPredicate> item = ChallengeUtils.readItemPredicate(buf);
            int count = buf.readVarInt();
            return new KillMobChallenge(entity, item, count);
        }

        @Override
        public Codec<KillMobChallenge> codec()
        {
            return KillMobChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EntityPredicate> entityPredicate;
        private final Optional<ItemPredicate> itemPredicate;

        private Tracker(int maxCount, Optional<EntityPredicate> entityPredicate, Optional<ItemPredicate> itemPredicate)
        {
            super(maxCount, ProgressFormatters.KILLED_X_OF_X);
            this.entityPredicate = entityPredicate;
            this.itemPredicate = itemPredicate;
        }

        private boolean test(LivingEntity entity, ItemStack stack, ServerPlayer player)
        {
            return this.entityPredicate.map(p -> p.matches(player, entity)).orElse(true) && this.itemPredicate.map(p -> p.matches(stack)).orElse(true);
        }

        public static void registerEvent()
        {
            EntityEvents.LIVING_ENTITY_DEATH.register((entity, source) -> {
                if(entity.level().isClientSide())
                    return false;

                Entity cause = source.getEntity();
                if(cause != null && cause.getType() == EntityType.PLAYER) {
                    ServerPlayer player = (ServerPlayer) cause;
                    UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                        if(tracker.isComplete())
                            return;
                        ItemStack heldItem = player.getMainHandItem();
                        if(tracker.test(entity, heldItem, player)) {
                            tracker.increment(player);
                        }
                    });
                }
                return false;
            });
        }
    }
}
