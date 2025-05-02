package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class AnimationTickValue implements Value
{
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("animation_tick"), MapCodec.unit(new AnimationTickValue()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        return context.animationTick() + context.partialTick();
    }
}
