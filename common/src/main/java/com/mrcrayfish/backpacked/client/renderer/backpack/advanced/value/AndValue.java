package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

public record AndValue(Value first, Value second, Operator operator) implements Value
{
    public static final Type TYPE = new Type(
        Utils.rl("and"),
        RecordCodecBuilder.<AndValue>mapCodec(builder -> builder.group(
            Value.EITHER_CODEC.fieldOf("first").forGetter(o -> o.first),
            Value.EITHER_CODEC.fieldOf("second").forGetter(o -> o.second),
            Operator.CODEC.fieldOf("op").orElse(Operator.ADD).forGetter(o -> o.operator)
        ).apply(builder, AndValue::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        return this.operator.function().apply(this.first.get(context), this.second.get(context));
    }
}
