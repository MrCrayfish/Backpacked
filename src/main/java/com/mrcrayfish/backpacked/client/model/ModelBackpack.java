package com.mrcrayfish.backpacked.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/**
 * Author: MrCrayfish
 */
public class ModelBackpack<T extends LivingEntity> extends BipedModel<T>
{
    public RendererModel backpack;

    public ModelBackpack()
    {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.backpack = new RendererModel(this, 0, 0);
        this.backpack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backpack.addBox(-3.5F, 1.0F, 2.0F, 7, 9, 4, 0.0F);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        GlStateManager.pushMatrix();
        if(entityIn.isSneaking())
        {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
        }
        this.backpack.render(scale);
        GlStateManager.popMatrix();
    }

    public void setupAngles(BipedModel<T> model)
    {
        copyProperties(model.bipedBody, this.backpack);
    }

    private static void copyProperties(RendererModel source, RendererModel target)
    {
        target.offsetX = source.offsetX;
        target.offsetY = source.offsetY;
        target.offsetZ = source.offsetZ;
        target.copyModelAngles(source);
    }
}
