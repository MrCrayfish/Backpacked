package com.mrcrayfish.backpacked.client.renderer.backpack.value.source;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class AnimationTickSource implements BaseSource
{
    public static final Type TYPE = new Type(new ResourceLocation("animation_tick"), Codec.unit(new AnimationTickSource()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double apply(BackpackRenderContext context)
    {
        return context.animationTick() + context.partialTick();
    }
}
