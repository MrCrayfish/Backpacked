package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.world.level.Level;

public class TimeOfDayValue implements Value
{
    public static final Type TYPE = new Type(Utils.rl("time_of_day"), MapCodec.unit(new TimeOfDayValue()));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        Level level = context.level();
        return level != null ? level.getTimeOfDay(context.partialTick()) : 0;
    }
}
