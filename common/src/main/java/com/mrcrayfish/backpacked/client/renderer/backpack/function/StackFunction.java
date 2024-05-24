package com.mrcrayfish.backpacked.client.renderer.backpack.function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public record StackFunction(List<BaseFunction> functions) implements BaseFunction
{
    public static final Type TYPE = new Type(new ResourceLocation("stack"), RecordCodecBuilder.<StackFunction>create(builder -> {
        return builder.group(
            BaseFunction.CODEC.listOf().fieldOf("functions").forGetter(o -> o.functions)
        ).apply(builder, StackFunction::new);
    }));

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
