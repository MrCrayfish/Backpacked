package com.mrcrayfish.backpacked.client.renderer;

import net.minecraft.client.resources.model.BakedModel;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface BackpackRenderer
{
    void draw(BakedModel model);
}
