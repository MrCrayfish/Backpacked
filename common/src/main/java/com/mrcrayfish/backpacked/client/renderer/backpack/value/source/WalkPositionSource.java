package com.mrcrayfish.backpacked.client.renderer.backpack.value.source;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class WalkPositionSource implements BaseSource
{
    public static final Type TYPE = new Type(new ResourceLocation("walk_position"), Codec.unit(new WalkPositionSource()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double apply(BackpackRenderContext context)
    {
        return context.entity() != null ? context.entity().walkAnimation.position(context.partialTick()) : 0;
    }
}
