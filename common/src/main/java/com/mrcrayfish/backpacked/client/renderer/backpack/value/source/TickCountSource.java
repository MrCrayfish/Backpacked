package com.mrcrayfish.backpacked.client.renderer.backpack.value.source;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class TickCountSource implements BaseSource
{
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("tick_count"), MapCodec.unit(new TickCountSource()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double apply(BackpackRenderContext context)
    {
        return context.entity() != null ? context.entity().tickCount + context.partialTick() : 0;
    }
}
