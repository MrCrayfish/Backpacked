package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends PlayerEntity, M extends BipedModel<T>> extends LayerRenderer<T, M>
{
    private ModelBackpack<T> model;
    private boolean backpackEnabled = false;

    public BackpackLayer(IEntityRenderer<T, M> renderer, ModelBackpack<T> model)
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
            ItemStack chestStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if(chestStack.getItem() == Items.ELYTRA)
            {
                setBackpackEnabled(false);
                return;
            }

            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
            {
                setBackpackEnabled(false);
                return;
            }
            setBackpackEnabled(true);
            stack.push();
            this.getEntityModel().setModelAttributes(this.model);
            this.model.setupAngles(this.getEntityModel());
            BackpackItem item = (BackpackItem) backpack.getItem();
            IVertexBuilder builder = ItemRenderer.getBuffer(renderTypeBuffer, this.model.getRenderType(item.getModelTexture()), false, backpack.hasEffect());
            this.model.render(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
            stack.pop();
        }
    }

    private void setBackpackEnabled(boolean enabled) {
        if (enabled == backpackEnabled) return;
        backpackEnabled = enabled;
        // Enable/Disable cape.
        Minecraft.getInstance().gameSettings.setModelPartEnabled(PlayerModelPart.CAPE, !enabled);
    }
}
