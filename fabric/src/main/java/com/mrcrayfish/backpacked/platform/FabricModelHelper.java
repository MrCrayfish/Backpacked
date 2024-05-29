package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class FabricModelHelper implements IModelHelper
{
    @Override
    public BakedModel getBakedModel(ResourceLocation id)
    {
        return Minecraft.getInstance().getModelManager().getModel(id);
    }
}
