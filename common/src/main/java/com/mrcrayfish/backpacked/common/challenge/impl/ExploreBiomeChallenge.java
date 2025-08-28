package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ExploreBiomeChallenge extends Challenge
{
    public static final Codec<List<ResourceKey<Biome>>> BIOME_LIST_CODEC = Codec.either(ResourceKey.codec(Registries.BIOME), ResourceKey.codec(Registries.BIOME).listOf()).xmap(either -> {
        return either.map(List::of, Function.identity());
    }, keys -> {
        return keys.size() == 1 ? Either.left(keys.getFirst()) : Either.right(keys);
    }).validate(keys -> {
        return keys.isEmpty() ? DataResult.error(() -> "Must specify at least one biome") : DataResult.success(keys);
    });
    public static final ChallengeSerializer<ExploreBiomeChallenge> SERIALIZER = new ChallengeSerializer<>(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "explore_biome"),
        RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(BIOME_LIST_CODEC.fieldOf("biome").forGetter(challenge -> {
                return challenge.biomes;
            })).apply(builder, ExploreBiomeChallenge::new);
        })
    );

    private final List<ResourceKey<Biome>> biomes;

    public ExploreBiomeChallenge(List<ResourceKey<Biome>> biomes)
    {
        super();
        this.biomes = biomes;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return new Tracker(formatter, this.biomes);
    }

    public static class Tracker implements IProgressTracker
    {
        private final ProgressFormatter formatter;
        private final ImmutableSet<ResourceKey<Biome>> biomes;
        private final Set<ResourceLocation> exploredBiomes = new HashSet<>();

        private Tracker(ProgressFormatter formatter, List<ResourceKey<Biome>> biomes)
        {
            this.formatter = formatter;
            this.biomes = ImmutableSet.copyOf(biomes);
        }

        private void explore(ResourceKey<Biome> biome, ServerPlayer player)
        {
            if(this.biomes.contains(biome))
            {
                this.exploredBiomes.add(biome.location());
                this.markForCompletionTest(player);
            }
        }

        @Override
        public boolean isComplete()
        {
            return this.exploredBiomes.size() >= this.biomes.size();
        }

        @Override
        public void read(CompoundTag tag)
        {
            this.exploredBiomes.clear();
            ListTag list = tag.getList("ExploredBiomes", Tag.TAG_STRING);
            list.forEach(nbt ->
            {
                ResourceLocation id = ResourceLocation.tryParse(nbt.getAsString());
                if(id != null && this.biomes.stream().anyMatch(key -> key.location().equals(id)))
                {
                    this.exploredBiomes.add(id);
                }
            });
        }

        @Override
        public void write(CompoundTag tag)
        {
            ListTag list = new ListTag();
            this.exploredBiomes.forEach(location -> list.add(StringTag.valueOf(location.toString())));
            tag.put("ExploredBiomes", list);
        }

        @Override
        public Component getDisplayComponent()
        {
            return this.formatter.formatter().apply(this.exploredBiomes.size(), this.biomes.size());
        }

        @Override
        public double getCompletionProgress()
        {
            return Mth.clamp(this.exploredBiomes.size() / (double) Math.max(1, this.biomes.size()), 0, 1);
        }

        public static void registerEvent()
        {
            BackpackedEvents.EXPLORE_UPDATE.register((key, player) -> {
                if(player.level().isClientSide())
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(!tracker.isComplete()) {
                        tracker.explore(key, (ServerPlayer) player);
                    }
                });
            });
        }
    }
}
