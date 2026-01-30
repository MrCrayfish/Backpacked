package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

/**
 * Author: MrCrayfish
 */
public record ConstantValue(double value) implements Value // TODO DONE
{
    public static final Type TYPE = new Type(
        Utils.rl("constant"),
        RecordCodecBuilder.<ConstantValue>create(builder -> builder.group(
            Codec.DOUBLE.fieldOf("value").forGetter(o -> o.value)
        ).apply(builder, ConstantValue::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        return this.value;
    }
}
