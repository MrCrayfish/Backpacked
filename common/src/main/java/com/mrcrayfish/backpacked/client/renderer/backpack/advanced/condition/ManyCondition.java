package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

import java.util.List;

public record ManyCondition(List<BaseCondition> conditions) implements BaseCondition
{
    public static final Type TYPE = new Type(Utils.rl("many"), RecordCodecBuilder.<ManyCondition>mapCodec(builder -> builder.group(
        BaseCondition.CODEC.listOf().fieldOf("conditions").forGetter(o -> o.conditions)
    ).apply(builder, ManyCondition::new)));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public boolean test(BackpackRenderContext context)
    {
        for(BaseCondition condition : this.conditions)
        {
            if(!condition.test(context))
            {
                return false;
            }
        }
        return true;
    }
}
