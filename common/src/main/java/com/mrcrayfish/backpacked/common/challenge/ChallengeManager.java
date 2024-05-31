package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.common.challenge.impl.*;
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

    private final Map<ResourceLocation, ChallengeSerializer<? extends Challenge>> serializers = new HashMap<>();

    private ChallengeManager()
    {
        this.register(KillMobChallenge.ID, KillMobChallenge.SERIALIZER);
        this.register(FeedAnimalChallenge.ID, FeedAnimalChallenge.SERIALIZER);
        this.register(BreedAnimalChallenge.ID, BreedAnimalChallenge.SERIALIZER);
        this.register(MineBlockChallenge.ID, MineBlockChallenge.SERIALIZER);
        this.register(InteractWithBlockChallenge.ID, InteractWithBlockChallenge.SERIALIZER);
        this.register(InteractWithEntityChallenge.ID, InteractWithEntityChallenge.SERIALIZER);
        this.register(TravelDistanceChallenge.ID, TravelDistanceChallenge.SERIALIZER);
        this.register(ExploreBiomeChallenge.ID, ExploreBiomeChallenge.SERIALIZER);
        this.register(CraftItemChallenge.ID, CraftItemChallenge.SERIALIZER);
        this.register(MerchantTradeChallenge.ID, MerchantTradeChallenge.SERIALIZER);
    }

    public <C extends Challenge, T extends ChallengeSerializer<C>> void register(ResourceLocation id, T serializer)
    {
        if(this.serializers.containsKey(id))
            throw new IllegalArgumentException("Challenge with the id '%s' already exists".formatted(id));
        this.serializers.put(id, serializer);
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
