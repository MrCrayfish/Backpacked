package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.world.entity.LivingEntity;

public class FallFlyingCondition implements BaseCondition
{
    public static final Type TYPE = new Type(Utils.rl("fall_flying"), MapCodec.unit(new FallFlyingCondition()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public boolean test(BackpackRenderContext context)
    {
        LivingEntity entity = context.entity();
        return entity != null && entity.isFallFlying();
    }
}
