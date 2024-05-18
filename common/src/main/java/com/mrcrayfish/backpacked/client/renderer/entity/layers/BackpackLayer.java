package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.ModelProperty;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation DEFAULT_BACKPACK = new ResourceLocation("");

    private final ItemRenderer itemRenderer;

    public BackpackLayer(RenderLayerParent<T, M> renderer, ItemRenderer itemRenderer)
    {
        super(renderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int light, T player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        ItemStack stack = Services.BACKPACK.getBackpackStack(player);
        if(stack.getItem() instanceof BackpackItem)
        {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if(chestStack.getItem() == Items.ELYTRA && !canRenderWithElytra(stack))
                return;

            if(Services.BACKPACK.isUsingThirdPartySlot() && !Services.BACKPACK.isBackpackVisible(player))
                return;

            String modelName = stack.getOrCreateTag().getString("BackpackModel");
            Backpack backpack = BackpackManager.instance().getBackpack(modelName);
            if(backpack == null)
                return;

            ResourceLocation location = backpack.getModel().orElse(DEFAULT_BACKPACK);
            BakedModel model = ClientServices.MODEL.getBakedModel(location);
            if(model == null)
                return;

            pose.pushPose();
            //backpack.transformToPlayerBody(this.getParentModel().body, !chestStack.isEmpty());
            //backpack.setupAngles(player, player.tickCount, partialTick);
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.scale(1.05F, -1.05F, -1.05F);
            int offset = !chestStack.isEmpty() ? 3 : 2;
            pose.translate(0, -0.06, offset * 0.0625);
            //pose.mulPose(Axis.);
            this.itemRenderer.render(stack, ItemDisplayContext.HEAD, false, pose, source, light, OverlayTexture.NO_OVERLAY, model);

            pose.popPose();
        }
    }

    public static boolean canRenderWithElytra(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(ModelProperty.SHOW_WITH_ELYTRA.getTagName());
    }

    public static boolean canShowEnchantmentGlint(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(ModelProperty.SHOW_GLINT.getTagName());
    }
}
