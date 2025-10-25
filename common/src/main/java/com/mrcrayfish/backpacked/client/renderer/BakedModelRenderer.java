package com.mrcrayfish.backpacked.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import java.util.List;

public class BakedModelRenderer
{
    private static final RandomSource RANDOM = RandomSource.create();

    public static void drawBakedModel(BakedModel model, PoseStack stack, MultiBufferSource source, int light, int overlay)
    {
        stack.pushPose();
        stack.translate(-0.5F, -0.5F, -0.5F);
        VertexConsumer consumer = source.getBuffer(Sheets.translucentCullBlockSheet());
        for(Direction direction : Direction.values())
        {
            pushQuads(stack, consumer, model.getQuads(null, direction, RANDOM), light, overlay);
        }
        pushQuads(stack, consumer, model.getQuads(null, null, RANDOM), light, overlay);
        stack.popPose();
    }

    private static void pushQuads(PoseStack stack, VertexConsumer consumer, List<BakedQuad> quads, int light, int overlay)
    {
        PoseStack.Pose pose = stack.last();
        for(BakedQuad quad : quads)
        {
            consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
        }
    }
}
