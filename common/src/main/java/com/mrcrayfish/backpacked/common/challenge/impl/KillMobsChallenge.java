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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class KillMobsChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "kill_mobs");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<KillMobsChallenge> CODEC = RecordCodecBuilder.create(builder -> {
       return builder.group(BackpackedCodecs.ENTITY_TYPE_LIST.fieldOf("entity").forGetter(challenge -> {
           return challenge.types;
       }), ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(challenge -> {
           return challenge.count;
       })).apply(builder, KillMobsChallenge::new);
    });

    private final ImmutableList<EntityType<?>> types;
    private final int count;

    public KillMobsChallenge(ImmutableList<EntityType<?>> types, int count)
    {
        super(ID);
        this.types = types;
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
        return new Tracker(this.count, this.types);
    }

    public static final class Serializer extends ChallengeSerializer<KillMobsChallenge>
    {
        @Override
        public void write(KillMobsChallenge challenge, FriendlyByteBuf buf)
        {
            buf.writeCollection(challenge.types, (buf1, type) -> {
                buf1.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(type));
            });
            buf.writeVarInt(challenge.count);
        }

        @Override
        public KillMobsChallenge read(FriendlyByteBuf buf)
        {
            List<EntityType<?>> list = buf.readList(buf1 -> {
                return BuiltInRegistries.ENTITY_TYPE.get(buf1.readResourceLocation());
            });
            int count = buf.readVarInt();
            return new KillMobsChallenge(ImmutableList.copyOf(list), count);
        }

        @Override
        public Codec<KillMobsChallenge> codec()
        {
            return KillMobsChallenge.CODEC;
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        public Tracker(int maxCount, List<EntityType<?>> types)
        {
            super(maxCount, ProgressFormatters.KILLED_X_OF_X);
            UnlockManager.instance().addEventListener(EventType.LIVING_ENTITY_DEATH, (entity, source) -> {
                if(this.isComplete() || entity.level().isClientSide())
                    return false;
                Entity cause = source.getEntity();
                if(cause != null && cause.getType() == EntityType.PLAYER) {
                    if(types.contains(entity.getType())) {
                        this.increment((ServerPlayer) cause);
                    }
                }
                return false;
            });
        }
    }
}
