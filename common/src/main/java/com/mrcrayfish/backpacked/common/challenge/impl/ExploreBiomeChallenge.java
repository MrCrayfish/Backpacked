package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.impl.BiomeExploreProgressTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ExploreBiomeChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "explore_biome");
    public static final Serializer SERIALIZER = new Serializer();
    public static final Codec<List<ResourceKey<Biome>>> BIOME_LIST_CODEC = ExtraCodecs.validate(Codec.either(ResourceKey.codec(Registries.BIOME), ResourceKey.codec(Registries.BIOME).listOf()).xmap(either -> {
        return either.map(List::of, Function.identity());
    }, keys -> {
        return keys.size() == 1 ? Either.left(keys.get(0)) : Either.right(keys);
    }), keys -> {
        if(keys.isEmpty()) {
            return DataResult.error(() -> "Must specify at least one biome");
        }
        return DataResult.success(keys);
    });
    public static final Codec<ExploreBiomeChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(BIOME_LIST_CODEC.fieldOf("biome").forGetter(challenge -> {
            return challenge.biomes;
        })).apply(builder, ExploreBiomeChallenge::new);
    });

    private final List<ResourceKey<Biome>> biomes;

    public ExploreBiomeChallenge(List<ResourceKey<Biome>> biomes)
    {
        super(ID);
        this.biomes = biomes;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker()
    {
        return new BiomeExploreProgressTracker(this.biomes);
    }

    public static class Serializer extends ChallengeSerializer<ExploreBiomeChallenge>
    {
        @Override
        public void write(ExploreBiomeChallenge challenge, FriendlyByteBuf buf)
        {
            buf.writeCollection(challenge.biomes, (buf1, key) -> {
                buf1.writeResourceLocation(key.location());
            });
        }

        @Override
        public ExploreBiomeChallenge read(FriendlyByteBuf buf)
        {
            List<ResourceKey<Biome>> biomes = buf.readList(buf1 -> {
                return ResourceKey.create(Registries.BIOME, buf1.readResourceLocation());
            });
            return new ExploreBiomeChallenge(biomes);
        }

        @Override
        public Codec<ExploreBiomeChallenge> codec()
        {
            return ExploreBiomeChallenge.CODEC;
        }
    }
}
