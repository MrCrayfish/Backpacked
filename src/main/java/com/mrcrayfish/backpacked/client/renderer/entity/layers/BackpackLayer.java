package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends PlayerEntity, M extends BipedModel<T>> extends LayerRenderer<T, M>
{
    private ModelBackpack<T> model;

    public BackpackLayer(IEntityRenderer<T, M> renderer, ModelBackpack<T> model)
    {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(T player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.getItem() instanceof BackpackItem)
        {
            ItemStack chestStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if(chestStack.getItem() == Items.ELYTRA)
            {
                return;
            }

            GlStateManager.pushMatrix();
            BackpackItem item = (BackpackItem) backpack.getItem();
            this.bindTexture(item.getModelTexture());
            this.model.setupAngles(this.getEntityModel());
            this.model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
