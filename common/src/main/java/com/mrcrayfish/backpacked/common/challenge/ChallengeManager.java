package com.mrcrayfish.backpacked.common.challenge;

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
        this.register(KillMobChallenge.SERIALIZER);
        this.register(FeedAnimalChallenge.SERIALIZER);
        this.register(BreedAnimalChallenge.SERIALIZER);
        this.register(MineBlockChallenge.SERIALIZER);
        this.register(InteractWithBlockChallenge.SERIALIZER);
        this.register(InteractWithEntityChallenge.SERIALIZER);
        this.register(TravelDistanceChallenge.SERIALIZER);
        this.register(ExploreBiomeChallenge.SERIALIZER);
        this.register(CraftItemChallenge.SERIALIZER);
        this.register(MerchantTradeChallenge.SERIALIZER);
    }

    public <C extends Challenge, T extends ChallengeSerializer<C>> void register(T serializer)
    {
        if(this.serializers.containsKey(serializer.id()))
            throw new IllegalArgumentException("Challenge serializer with the id '%s' already exists".formatted(serializer.id()));
        this.serializers.put(serializer.id(), serializer);
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
