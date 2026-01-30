package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonObject;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.event.EntityEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class KillMobChallenge extends Challenge // TODO DONE
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "kill_mob");
    public static final Serializer SERIALIZER = new Serializer();

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
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return new Tracker(this.count, formatter, this.entity, this.item);
    }

    public static final class Serializer extends ChallengeSerializer<KillMobChallenge>
    {
        @Override
        public KillMobChallenge deserialize(JsonObject object)
        {
            Optional<EntityPredicate> entity = object.has("mob") ? Optional.of(EntityPredicate.fromJson(object.get("mob"))) : Optional.empty();
            Optional<ItemPredicate> item = object.has("item") ? Optional.of(ItemPredicate.fromJson(object.get("item"))) : Optional.empty();
            int count = readCount(object, 1);
            return new KillMobChallenge(entity, item, count);
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<EntityPredicate> entityPredicate;
        private final Optional<ItemPredicate> itemPredicate;

        private Tracker(int maxCount, ProgressFormatter formatter, Optional<EntityPredicate> entityPredicate, Optional<ItemPredicate> itemPredicate)
        {
            super(maxCount, formatter);
            this.entityPredicate = entityPredicate;
            this.itemPredicate = itemPredicate;
        }

        private boolean test(LivingEntity entity, ItemStack stack, ServerPlayer player)
        {
            return this.entityPredicate.map(p -> p.matches(player, entity)).orElse(true) && this.itemPredicate.map(p -> p.matches(stack)).orElse(true);
        }

        // Called via LivingEntityMixin and ServerPlayerMixin
        public static void onLivingEntityDeath(LivingEntity entity, DamageSource source)
        {
            Entity cause = source.getEntity();
            if(cause != null && cause.getType() == EntityType.PLAYER)
            {
                ServerPlayer player = (ServerPlayer) cause;
                UnlockManager.getIncompleteTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete())
                        return;
                    ItemStack heldItem = player.getMainHandItem();
                    if(tracker.test(entity, heldItem, player)) {
                        tracker.increment(player);
                    }
                });
            }
        }
    }
}
