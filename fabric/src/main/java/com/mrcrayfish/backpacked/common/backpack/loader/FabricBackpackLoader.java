package com.mrcrayfish.backpacked.common.backpack.loader;

import com.google.gson.JsonElement;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.util.Utils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class FabricBackpackLoader extends BackpackLoader implements IdentifiableResourceReloadListener
{
    public static final ResourceLocation ID = Utils.rl("backpack_loader");

    public FabricBackpackLoader(HolderLookup.Provider provider)
    {
        super(provider);
    }

    @Override
    public ResourceLocation getFabricId()
    {
        return ID;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler)
    {
        super.apply(map, manager, filler);
    }
}
