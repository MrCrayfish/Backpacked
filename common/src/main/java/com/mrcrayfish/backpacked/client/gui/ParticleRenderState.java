package com.mrcrayfish.backpacked.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public record ParticleRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, double width, double height, float u1, float v1, float u2, float v2, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState
{
    @Override
    public void buildVertices(VertexConsumer consumer)
    {
        consumer.addVertexWith2DPose(this.pose, 0, 0).setUv(this.u1, this.v1).setColor(0xFFFFFFFF);
        consumer.addVertexWith2DPose(this.pose, 0, (float) this.height).setUv(this.u1, this.v2).setColor(0xFFFFFFFF);
        consumer.addVertexWith2DPose(this.pose, (float) this.width, (float) this.height).setUv(this.u2, this.v2).setColor(0xFFFFFFFF);
        consumer.addVertexWith2DPose(this.pose, (float) this.width, 0).setUv(this.u2, this.v1).setColor(0xFFFFFFFF);
    }
}
