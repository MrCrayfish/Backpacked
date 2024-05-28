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
public class InteractWithEntityChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "interact_with_entity");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<InteractWithEntityChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "entity").forGetter(challenge -> {
            return challenge.entity;
        }), ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(challenge -> {
            return challenge.item;
        }), ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, InteractWithEntityChallenge::new);
    });

    private final Optional<EntityPredicate> entity;
    private final Optional<ItemPredicate> item;
    private final int count;

    public InteractWithEntityChallenge(Optional<EntityPredicate> entity, Optional<ItemPredicate> item, int count)
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
        return new Tracker(backpackId, this.count, this.entity, this.item);
    }

    public static class Serializer extends ChallengeSerializer<InteractWithEntityChallenge>
    {
        @Override
        public void write(InteractWithEntityChallenge challenge, FriendlyByteBuf buf)
        {
            ChallengeUtils.writeEntityPredicate(buf, challenge.entity);
            ChallengeUtils.writeItemPredicate(buf, challenge.item);
            buf.writeVarInt(challenge.count);
        }

        @Override
        public InteractWithEntityChallenge read(FriendlyByteBuf buf)
        {
            Optional<EntityPredicate> entity = ChallengeUtils.readEntityPredicate(buf);
            Optional<ItemPredicate> item = ChallengeUtils.readItemPredicate(buf);
            int count = buf.readVarInt();
            return new InteractWithEntityChallenge(entity, item, count);
        }

        @Override
        public Codec<InteractWithEntityChallenge> codec()
        {
            return InteractWithEntityChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final ResourceLocation backpackId;
        private final Optional<EntityPredicate> entity;
        private final Optional<ItemPredicate> item;

        private Tracker(ResourceLocation backpackId, int maxCount, Optional<EntityPredicate> entity, Optional<ItemPredicate> item)
        {
            super(maxCount, ProgressFormatters.MINED_X_OF_X);
            this.backpackId = backpackId;
            this.entity = entity;
            this.item = item;
        }

        private boolean test(ServerPlayer player, Entity entity, ItemStack stack)
        {
            return ChallengeUtils.testPredicate(this.entity, player, entity) && ChallengeUtils.testPredicate(this.item, stack);
        }

        public static void registerEvent()
        {
            // We want to test the entity before the interaction.
            BackpackedEvents.INTERACTED_WITH_ENTITY_CAPTURE.register((player, stack, entity, consumer) -> {
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete() && tracker.test(player, entity, stack)) {
                        consumer.accept(tracker.backpackId);
                    }
                });
            });

            BackpackedEvents.INTERACTED_WITH_ENTITY.register((player, stack, entity, callbacks) -> {
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    // We don't need to test the predicates again
                    if(!tracker.isComplete() && callbacks.contains(tracker.backpackId)) {
                        tracker.increment(player);
                    }
                });
            });
        }
    }
}
