package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

/**
 * Author: MrCrayfish
 */
public class TickCountValue implements Value
{
    public static final Type TYPE = new Type(Utils.rl("tick_count"), Codec.unit(new TickCountValue()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        return context.tickCount() + context.partialTick();
    }
}
