package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer extends RenderLayer<AvatarRenderState, PlayerModel>
{
    public BackpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer)
    {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int i, AvatarRenderState state, float v, float v1)
    {
        BackpackRenderState backpackRenderState = ClientServices.CLIENT.getBackpackRenderState(state);
        if(backpackRenderState == null || !backpackRenderState.visible)
            return;

        CosmeticProperties cosmeticProperties = backpackRenderState.cosmeticProperties;
        if(cosmeticProperties == null)
            return;

        ItemStack chestStack = state.chestEquipment;
        if(chestStack.is(Items.ELYTRA) && !cosmeticProperties.showWithElytra())
            return;

        poseStack.pushPose();

        // Transforms the pose to player's body
        this.getParentModel().body.translateAndRotate(poseStack);

        // Apply transforms to fix rotation and inverted model
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(1.05F, -1.05F, -1.05F);
        int offset = !chestStack.isEmpty() ? 3 : 2;
        poseStack.translate(0, -0.06, offset * 0.0625);

        poseStack.pushPose();

        // Applies a bobbing animation when the player is walking
        if(backpackRenderState.bobbing)
        {
            double animationScale = Mth.clamp(backpackRenderState.horizontalDelta * 5, 0, 1);
            double bob = (Mth.cos(state.walkAnimationPos) + 1) / 2 * 0.05;
            poseStack.translate(0, bob * animationScale, 0);
            double sway = Mth.cos(state.walkAnimationPos * 0.5F) * 3;
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (sway * animationScale)));
        }

        // Draw the backpack model
        BackpackRenderer renderer = backpackRenderState.renderer;
        if(renderer != null)
        {
            BackpackRenderContext context = new BackpackRenderContext(
                Scene.ON_ENTITY,
                RenderMode.ALL,
                poseStack,
                backpackRenderState.baseModel,
                backpackRenderState.strapsModel,
                backpackRenderState.entityData,
                backpackRenderState.levelData,
                state.id,
                state.lightCoords,
                (int) state.ageInTicks,
                state.ageInTicks - (int) state.ageInTicks,
                model -> {
                    poseStack.pushPose();
                    poseStack.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.submitDraw(collector, model, poseStack, 1.0F, 1.0F, 1.0F, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                });
            renderer.render(context);
        }
        else
        {
            this.drawStandaloneModel(backpackRenderState.baseModel, poseStack, collector, state.lightCoords);
        }

        poseStack.popPose();

        // Draw backpack straps
        this.drawStandaloneModel(backpackRenderState.strapsModel, poseStack, collector, state.lightCoords);

        poseStack.popPose();
    }

    private void drawStandaloneModel(Identifier id, PoseStack poseStack, SubmitNodeCollector collector, int light)
    {
        FrameworkModelResource<FrameworkBakedModel> resource = StandaloneModels.getResource(id);
        if(resource != null)
        {
            FrameworkBakedModel model = resource.getModel();
            if(model != null)
            {
                poseStack.pushPose();
                poseStack.translate(-0.5F, -0.5F, -0.5F);
                StandaloneModelRenderer.submitDraw(collector, model, poseStack, 1.0F, 1.0F, 1.0F, light, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }
}
