package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public record PushMatrixFunction(List<BaseFunction> functions) implements BaseFunction
{
    public static final Type TYPE = new Type(
        Utils.rl("push_matrix"),
        RecordCodecBuilder.<PushMatrixFunction>mapCodec(builder -> builder.group(
            BaseFunction.CODEC.listOf().fieldOf("functions").forGetter(o -> o.functions)
        ).apply(builder, PushMatrixFunction::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        PoseStack pose = context.pose();
        pose.pushPose();
        this.functions.forEach(f -> f.apply(context));
        pose.popPose();
    }
}
