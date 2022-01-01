package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * Author: MrCrayfish
 */
public class ModStats
{
    public static final ResourceLocation GATHER_HONEY = new ResourceLocation(Reference.MOD_ID, "gather_honey");

    public static void register()
    {
        registerStat(GATHER_HONEY);
    }

    private static void registerStat(ResourceLocation id)
    {
        Registry.register(Registry.CUSTOM_STAT, id.getPath(), id);
        Stats.CUSTOM.get(id, IStatFormatter.DEFAULT);
    }
}
