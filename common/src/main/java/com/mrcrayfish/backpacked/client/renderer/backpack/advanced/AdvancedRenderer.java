package com.mrcrayfish.backpacked.client.renderer.backpack.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.BaseFunction;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AdvancedRenderer(List<BaseFunction> functions) implements BackpackRenderer
{
    public static final Type TYPE = new Type(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "advanced"),
        RecordCodecBuilder.<AdvancedRenderer>mapCodec(builder -> builder.group(
            BaseFunction.CODEC.listOf().fieldOf("functions").forGetter(o -> o.functions)
        ).apply(builder, AdvancedRenderer::new))
    );

    @Override
    public void render(BackpackRenderContext context)
    {
        PoseStack pose = context.pose();
        pose.pushPose();
        this.functions.forEach(function -> function.apply(context));
        pose.popPose();
    }

    @Override
    public Type type()
    {
        return TYPE;
    }
}
