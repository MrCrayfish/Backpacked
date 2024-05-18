package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class ForgeModelHelper implements IModelHelper
{
    @Override
    @Nullable
    public BakedModel getBakedModel(ResourceLocation id)
    {
        return Minecraft.getInstance().getModelManager().getModel(id);
    }
}
