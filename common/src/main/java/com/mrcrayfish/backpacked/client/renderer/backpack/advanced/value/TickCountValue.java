package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.world.entity.LivingEntity;

/**
 * Author: MrCrayfish
 */
public class TickCountValue implements Value
{
    public static final Type TYPE = new Type(Utils.rl("tick_count"), MapCodec.unit(new TickCountValue()));

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
