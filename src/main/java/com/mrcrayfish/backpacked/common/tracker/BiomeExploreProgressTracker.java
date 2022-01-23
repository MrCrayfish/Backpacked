package com.mrcrayfish.backpacked.common.tracker;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class BiomeExploreProgressTracker implements IProgressTracker
{
    private final ImmutableSet<RegistryKey<Biome>> biomes;
    private final Set<ResourceLocation> exploredBiomes = new HashSet<>();

    @SafeVarargs
    public BiomeExploreProgressTracker(RegistryKey<Biome> ... biomes)
    {
        this.biomes = ImmutableSet.copyOf(Arrays.asList(biomes));
    }

    public void explore(RegistryKey<Biome> biome, ServerPlayerEntity player)
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
    public void read(CompoundNBT tag)
    {
        this.exploredBiomes.clear();
        ListNBT list = tag.getList("ExploredBiomes", Constants.NBT.TAG_STRING);
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
    public void write(CompoundNBT tag)
    {
        ListNBT list = new ListNBT();
        this.exploredBiomes.forEach(location -> list.add(StringNBT.valueOf(location.toString())));
        tag.put("ExploredBiomes", list);
    }

    @Override
    public ITextComponent getDisplayComponent()
    {
        return ProgressFormatters.EXPLORED_X_OF_X.apply(this.exploredBiomes.size(), this.biomes.size());
    }
}
