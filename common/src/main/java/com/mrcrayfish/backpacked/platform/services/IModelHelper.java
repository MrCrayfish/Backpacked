package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public interface IModelHelper
{
    BakedModel getBakedModel(ResourceLocation id);
}
