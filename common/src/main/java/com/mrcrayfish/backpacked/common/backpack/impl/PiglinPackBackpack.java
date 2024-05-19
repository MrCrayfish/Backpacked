package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.impl.BiomeExploreProgressTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class PiglinPackBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "piglin_pack");

    public PiglinPackBackpack()
    {
        super(null);
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new BiomeExploreProgressTracker(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS);
    }
}
