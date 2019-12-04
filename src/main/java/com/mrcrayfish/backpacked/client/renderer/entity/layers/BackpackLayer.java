package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;

import java.util.concurrent.atomic.AtomicReference;

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
        if(!Backpacked.getBackpackStack(player).isEmpty())
        {
            GlStateManager.pushMatrix();
            this.bindTexture(TEXTURE);
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
