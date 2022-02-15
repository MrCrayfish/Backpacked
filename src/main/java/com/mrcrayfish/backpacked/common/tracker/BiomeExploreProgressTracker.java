package com.mrcrayfish.backpacked.common.tracker;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class BiomeExploreProgressTracker implements IProgressTracker
{
    private final ImmutableSet<ResourceKey<Biome>> biomes;
    private final Set<ResourceLocation> exploredBiomes = new HashSet<>();

    @SafeVarargs
    public BiomeExploreProgressTracker(ResourceKey<Biome> ... biomes)
    {
        this.biomes = ImmutableSet.copyOf(Arrays.asList(biomes));
    }

    public void explore(ResourceKey<Biome> biome, ServerPlayer player)
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
        return ProgressFormatters.EXPLORED_X_OF_X.apply(this.exploredBiomes.size(), this.biomes.size());
    }
}
