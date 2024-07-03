package com.mrcrayfish.backpacked.common.backpack.loader;

import com.google.gson.JsonElement;
import com.mrcrayfish.backpacked.Constants;
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
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack_loader");
    private static WeakReference<HolderLookup.Provider> providerRef;

    public static void setProvider(HolderLookup.Provider provider)
    {
        FabricBackpackLoader.providerRef = new WeakReference<>(provider);
    }

    public FabricBackpackLoader()
    {
        super(null);
    }

    @Override
    public ResourceLocation getFabricId()
    {
        return ID;
    }

    @Override
    protected HolderLookup.Provider getProvider()
    {
        if(providerRef == null || providerRef.get() == null)
            throw new IllegalStateException("Provider not available");
        HolderLookup.Provider provider = providerRef.get();
        return Objects.requireNonNull(provider);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler)
    {
        super.apply(map, manager, filler);
        providerRef.clear();
    }
}
