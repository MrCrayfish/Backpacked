package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer extends RenderLayer<AvatarRenderState, PlayerModel>
{
    private final ItemModelResolver itemModelResolver;

    public BackpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, ItemModelResolver itemModelResolver)
    {
        super(renderer);
        this.itemModelResolver = itemModelResolver;
    }

    @Override
    public void submit(PoseStack stack, SubmitNodeCollector collector, int i, AvatarRenderState state, float v, float v1)
    {
        // TODO 1.21.11 restore
        /*Optional<CosmeticProperties> propertiesOptional = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player);
        if(propertiesOptional.isEmpty())
            return;

        CosmeticProperties properties = propertiesOptional.get();
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if(chestStack.getItem() == Items.ELYTRA && !properties.showWithElytra())
            return;

        if(!Services.BACKPACK.isBackpackVisible(player))
            return;

        Identifier cosmeticId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(cosmeticId);
        if(backpack == null)
            return;

        pose.pushPose();

        // Transforms the pose to player's body
        this.getParentModel().body.translateAndRotate(pose);

        // Apply transforms to fix rotation and inverted model
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        pose.scale(1.05F, -1.05F, -1.05F);
        int offset = !chestStack.isEmpty() ? 3 : 2;
        pose.translate(0, -0.06, offset * 0.0625);

        pose.pushPose();

        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);

        // Applies a bobbing animation when the player is walking
        if(meta.bobbing())
        {
            double animationScale = Mth.clamp(player.getDeltaMovement().horizontalDistance() * 5, 0, 1);
            double bob = (Mth.cos(player.walkAnimation.position(partialTick)) + 1) / 2 * 0.05;
            pose.translate(0, bob * animationScale, 0);
            double sway = Mth.cos(player.walkAnimation.position(partialTick) * 0.5F) * 3;
            pose.mulPose(Axis.ZP.rotationDegrees((float) (sway * animationScale)));
        }

        // Draw the backpack model
        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(Scene.ON_ENTITY, RenderMode.ALL, pose, source, light, backpack, player, player.level(), partialTick, model -> {
                BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
            }, player.tickCount);
            pose.pushPose();
            renderer.render(context);
            pose.popPose();
        }, () -> {
            //BakedModel model = this.getModel(backpack.getBaseModel());
            //BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
        });

        pose.popPose();

        BakedModelRenderer.drawBakedModel(this.getModel(backpack.getStrapsModel()), pose, source, light, OverlayTexture.NO_OVERLAY);

        pose.popPose();*/
    }
}
