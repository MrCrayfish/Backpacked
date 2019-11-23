package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import com.mrcrayfish.backpacked.entity.player.ExtendedPlayerInventory;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends PlayerEntity, M extends BipedModel<T>> extends LayerRenderer<T, M>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/backpack.png");

    private ModelBackpack<T> model;

    public BackpackLayer(IEntityRenderer<T, M> renderer, ModelBackpack<T> model)
    {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(T player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn)
    {
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            ExtendedPlayerInventory inventory = (ExtendedPlayerInventory) player.inventory;
            if(!inventory.getBackpackItems().get(0).isEmpty())
            {
                GlStateManager.pushMatrix();
                this.bindTexture(TEXTURE);
                this.model.setupAngles(this.getEntityModel());
                this.model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
