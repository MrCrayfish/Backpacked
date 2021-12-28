package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.client.model.StandardBackpackModel;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends PlayerEntity, M extends BipedModel<T>> extends LayerRenderer<T, M>
{
    private StandardBackpackModel model;

    public BackpackLayer(IEntityRenderer<T, M> renderer, StandardBackpackModel model)
    {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.getItem() instanceof BackpackItem)
        {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlotType.CHEST);
            if(chestStack.getItem() == Items.ELYTRA)
                return;

            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
                return;

            stack.pushPose();
            this.model.setupAngles(this.getParentModel().body, !chestStack.isEmpty());
            BackpackItem item = (BackpackItem) backpack.getItem();
            IVertexBuilder builder = ItemRenderer.getFoilBuffer(renderTypeBuffer, this.model.renderType(item.getModelTexture()), false, backpack.hasFoil());
            this.model.renderToBuffer(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
            stack.popPose();
        }
    }
}
