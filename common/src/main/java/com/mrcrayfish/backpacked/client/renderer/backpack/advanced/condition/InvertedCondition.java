package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

public record InvertedCondition(BaseCondition condition) implements BaseCondition
{
    public static final Type TYPE = new Type(Utils.rl("inverted"), RecordCodecBuilder.<InvertedCondition>mapCodec(builder -> builder.group(
        BaseCondition.CODEC.fieldOf("condition").forGetter(o -> o.condition)
    ).apply(builder, InvertedCondition::new)));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public boolean test(BackpackRenderContext context)
    {
        return !this.condition.test(context);
    }
}
