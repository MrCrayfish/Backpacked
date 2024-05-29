package com.mrcrayfish.backpacked.common.backpack.loader;

import com.mrcrayfish.backpacked.Constants;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class FabricBackpackLoader extends BackpackLoader implements IdentifiableResourceReloadListener
{
    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "backpack_loader");

    @Override
    public ResourceLocation getFabricId()
    {
        return ID;
    }
}
