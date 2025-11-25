package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.BakedModelRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M>
{
    private final ItemRenderer itemRenderer;

    public BackpackLayer(RenderLayerParent<T, M> renderer, ItemRenderer itemRenderer)
    {
        super(renderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int light, T player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        Optional<CosmeticProperties> propertiesOptional = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player);
        if(propertiesOptional.isEmpty())
            return;

        CosmeticProperties properties = propertiesOptional.get();
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if(chestStack.getItem() == Items.ELYTRA && !properties.showWithElytra())
            return;

        if(!Services.BACKPACK.isBackpackVisible(player))
            return;

        ResourceLocation cosmeticId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
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
            BakedModel model = this.getModel(backpack.getBaseModel());
            BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
        });

        pose.popPose();

        BakedModelRenderer.drawBakedModel(this.getModel(backpack.getStrapsModel()), pose, source, light, OverlayTexture.NO_OVERLAY);

        pose.popPose();
    }

    private BakedModel getModel(ModelResourceLocation location)
    {
        return this.itemRenderer.getItemModelShaper().getModelManager().getModel(location);
    }
}
