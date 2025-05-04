package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

public record AndCondition(BaseCondition first, BaseCondition second) implements BaseCondition
{
    public static final Type TYPE = new Type(Utils.rl("and"), RecordCodecBuilder.<AndCondition>mapCodec(builder -> builder.group(
        BaseCondition.CODEC.fieldOf("first").forGetter(o -> o.first),
        BaseCondition.CODEC.fieldOf("second").forGetter(o -> o.second)
    ).apply(builder, AndCondition::new)));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public boolean test(BackpackRenderContext context)
    {
        return this.first.test(context) && this.second.test(context);
    }
}
