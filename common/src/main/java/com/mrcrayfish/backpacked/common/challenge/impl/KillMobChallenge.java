package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.BackpackedCodecs;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.EventType;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
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
       return builder.group(BackpackedCodecs.ENTITY_TYPE_LIST.fieldOf("mob").forGetter(challenge -> {
           return challenge.types;
       }), ItemPredicate.CODEC.optionalFieldOf("using_item").forGetter(challenge -> {
           return challenge.predicate;
       }), ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(challenge -> {
           return challenge.count;
       })).apply(builder, KillMobChallenge::new);
    });

    private final ImmutableList<EntityType<?>> types;
    private final Optional<ItemPredicate> predicate;
    private final int count;

    public KillMobChallenge(ImmutableList<EntityType<?>> types, Optional<ItemPredicate> predicate, int count)
    {
        super(ID);
        this.types = types;
        this.predicate = predicate;
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
        return new Tracker(this.count, this.types, this.predicate);
    }

    public static final class Serializer extends ChallengeSerializer<KillMobChallenge>
    {
        @Override
        public void write(KillMobChallenge challenge, FriendlyByteBuf buf)
        {
            buf.writeCollection(challenge.types, (buf1, type) -> {
                buf1.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(type));
            });
            buf.writeOptional(challenge.predicate, (buf1, predicate) -> {
                buf1.writeNbt(ItemPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                    .getOrThrow(false, Constants.LOG::error));
            });
            buf.writeVarInt(challenge.count);
        }

        @Override
        public KillMobChallenge read(FriendlyByteBuf buf)
        {
            List<EntityType<?>> list = buf.readList(buf1 -> {
                return BuiltInRegistries.ENTITY_TYPE.get(buf1.readResourceLocation());
            });
            Optional<ItemPredicate> predicate = buf.readOptional(buf1 -> {
                return ItemPredicate.CODEC.parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
                    .getOrThrow(false, Constants.LOG::error);
            });
            int count = buf.readVarInt();
            return new KillMobChallenge(ImmutableList.copyOf(list), predicate, count);
        }

        @Override
        public Codec<KillMobChallenge> codec()
        {
            return KillMobChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        public Tracker(int maxCount, List<EntityType<?>> types, Optional<ItemPredicate> predicate)
        {
            super(maxCount, ProgressFormatters.KILLED_X_OF_X);
            UnlockManager.instance().addEventListener(EventType.LIVING_ENTITY_DEATH, (entity, source) -> {
                if(this.isComplete() || entity.level().isClientSide())
                    return false;
                Entity cause = source.getEntity();
                if(cause != null && cause.getType() == EntityType.PLAYER) {
                    if(types.contains(entity.getType())) {
                        ServerPlayer player = (ServerPlayer) cause;
                        ItemStack heldItem = player.getMainHandItem();
                        if(predicate.map(p -> p.matches(heldItem)).orElse(true)) {
                            this.increment((ServerPlayer) cause);
                        }
                    }
                }
                return false;
            });
        }
    }
}
