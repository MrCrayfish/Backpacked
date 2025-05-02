package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class WalkSpeedValue implements Value
{
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("walk_speed"), MapCodec.unit(new WalkSpeedValue()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        return context.entity() != null ? context.entity().walkAnimation.speed(context.partialTick()) : 0;
    }
}
