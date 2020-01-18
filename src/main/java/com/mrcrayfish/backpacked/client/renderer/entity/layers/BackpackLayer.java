package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer implements LayerRenderer<EntityPlayer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/backpack.png");

    private RenderPlayer renderer;
    private ModelBackpack model = new ModelBackpack();

    public BackpackLayer(RenderPlayer renderer)
    {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack backpackStack = Backpacked.getBackpackStack(entity);
        if(!backpackStack.isEmpty())
        {
            this.renderer.bindTexture(TEXTURE);
            this.model.setModelAttributes(this.renderer.getMainModel());
            this.model.setupAngles(this.renderer.getMainModel());
            this.model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
