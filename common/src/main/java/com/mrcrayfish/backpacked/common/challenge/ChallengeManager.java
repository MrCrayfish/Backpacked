package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.impl.FeedAnimalsChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.KillMobsChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.MineBlocksChallenge;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public final class ChallengeManager
{
    private static ChallengeManager instance;

    public static ChallengeManager instance()
    {
        if(instance == null)
        {
            instance = new ChallengeManager();
        }
        return instance;
    }

    private final Map<ResourceLocation, Codec<? extends Challenge>> codecs = new HashMap<>();
    private final Map<ResourceLocation, ChallengeSerializer<? extends Challenge>> serializers = new HashMap<>();

    private ChallengeManager()
    {
        this.register(KillMobsChallenge.ID, KillMobsChallenge.CODEC, KillMobsChallenge.SERIALIZER);
        this.register(FeedAnimalsChallenge.ID, FeedAnimalsChallenge.CODEC, FeedAnimalsChallenge.SERIALIZER);
        this.register(MineBlocksChallenge.ID, MineBlocksChallenge.CODEC, MineBlocksChallenge.SERIALIZER);
    }

    public <C extends Challenge, T extends ChallengeSerializer<C>> void register(ResourceLocation id, Codec<C> codec, T serializer)
    {
        if(this.codecs.containsKey(id))
            throw new IllegalArgumentException("Challenge with the id '%s' already exists".formatted(id));
        this.codecs.put(id, codec);
        this.serializers.put(id, serializer);
    }

    @Nullable
    public Codec<? extends Challenge> getCodec(ResourceLocation id)
    {
        return this.codecs.get(id);
    }

    @Nullable
    public ChallengeSerializer<? extends Challenge> getSerializer(ResourceLocation id)
    {
        return this.serializers.get(id);
    }

    public ResourceLocation getSerializerId(ChallengeSerializer<?> serializer)
    {
        return this.serializers.entrySet().stream()
            .filter(entry -> entry.getValue() == serializer)
            .map(Map.Entry::getKey)
            .findFirst().orElse(null);
    }
}
