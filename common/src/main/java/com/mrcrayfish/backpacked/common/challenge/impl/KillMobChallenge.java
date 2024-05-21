package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.common.collect.ImmutableList;
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
import com.mrcrayfish.backpacked.event.EventType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
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
       return builder.group(EntityPredicate.CODEC.optionalFieldOf("target").forGetter(challenge -> {
           return challenge.entity;
       }), ItemPredicate.CODEC.optionalFieldOf("using_item").forGetter(challenge -> {
           return challenge.item;
       }), ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(challenge -> {
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
    public IProgressTracker createProgressTracker()
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
        public Tracker(int maxCount, Optional<EntityPredicate> entityPredicate, Optional<ItemPredicate> itemPredicate)
        {
            super(maxCount, ProgressFormatters.KILLED_X_OF_X);
            UnlockManager.instance().addEventListener(EventType.LIVING_ENTITY_DEATH, (livingEntity, source) -> {
                if(this.isComplete() || livingEntity.level().isClientSide())
                    return false;
                Entity cause = source.getEntity();
                if(cause != null && cause.getType() == EntityType.PLAYER) {
                    ServerPlayer player = (ServerPlayer) cause;
                    if(entityPredicate.map(predicate -> predicate.matches(player, livingEntity)).orElse(true)) {
                        ItemStack heldItem = player.getMainHandItem();
                        if(itemPredicate.map(p -> p.matches(heldItem)).orElse(true)) {
                            this.increment((ServerPlayer) cause);
                        }
                    }
                }
                return false;
            });
        }
    }
}
