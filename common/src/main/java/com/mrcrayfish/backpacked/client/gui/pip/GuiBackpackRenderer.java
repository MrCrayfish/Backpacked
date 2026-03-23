package com.mrcrayfish.backpacked.client.gui.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.util.Brightness;
import net.minecraft.util.LightCoordsUtil;

import java.util.Objects;

public class GuiBackpackRenderer extends PictureInPictureRenderer<GuiBackpackRenderState>
{
    public GuiBackpackRenderer(MultiBufferSource.BufferSource source)
    {
        super(source);
    }

    @Override
    public Class<GuiBackpackRenderState> getRenderStateClass()
    {
        return GuiBackpackRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiBackpackRenderState state, PoseStack pose)
    {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

        pose.pushPose();
        pose.scale(1.0F, -1.0F, -1.0F);
        ScreenRectangle bounds = state.bounds();
        Objects.requireNonNull(bounds);
        float offset = (bounds.height() / 2.0F) / state.scale();
        pose.translate(0, offset, 0);

        ItemTransform transform = state.transform();
        if(transform != null)
        {
            transform.apply(false, pose.last());
            pose.translate(0.5F, 0.5F, 0.5F); // Fix translation added by the above transform
        }

        BackpackRenderer renderer = state.renderer();
        if(renderer != null)
        {
            BackpackRenderContext context = new BackpackRenderContext(Scene.CUSTOMISATION_MENU, RenderMode.MODELS_ONLY, pose, state.baseModel(), state.strapsModel(), state.entityData(), state.levelData(), state.entityId(), 0xFFF000F0, state.tickCount(), state.partialTick(), model -> {
                pose.pushPose();
                pose.translate(-0.5F, -0.5F, -0.5F);
                StandaloneModelRenderer.draw(model, pose, this.bufferSource, 1.0F, 1.0F, 1.0F, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                pose.popPose();
            });
            renderer.render(context);
        }
        else
        {
            FrameworkModelResource<FrameworkBakedModel> resource = StandaloneModels.getResource(state.baseModel());
            if(resource != null)
            {
                FrameworkBakedModel model = resource.getModel();
                if(model != null)
                {
                    pose.pushPose();
                    pose.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.draw(model, pose, this.bufferSource, 1.0F, 1.0F, 1.0F, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                    pose.popPose();
                }
            }
        }

        pose.popPose();
    }

    @Override
    protected String getTextureLabel()
    {
        return "backpacked backpack";
    }
}
