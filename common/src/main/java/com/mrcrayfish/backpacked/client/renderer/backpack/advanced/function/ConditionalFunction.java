package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition.BaseCondition;
import com.mrcrayfish.backpacked.util.Utils;

import java.util.List;

public record ConditionalFunction(BaseCondition condition, List<BaseFunction> functions) implements BaseFunction
{
    public static final Type TYPE = new Type(
        Utils.rl("conditional"),
        RecordCodecBuilder.<ConditionalFunction>mapCodec(builder -> builder.group(
            BaseCondition.CODEC.fieldOf("condition").forGetter(o -> o.condition),
            BaseFunction.CODEC.listOf().fieldOf("functions").forGetter(o -> o.functions)
        ).apply(builder, ConditionalFunction::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        if(this.condition.test(context))
        {
            for(BaseFunction function : this.functions)
            {
                function.apply(context);
            }
        }
    }
}
