package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
    public void func_225628_a_(MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        ItemStack backpackStack = Backpacked.getBackpackStack(player);
        if(!backpackStack.isEmpty())
        {
            stack.push();
            this.getEntityModel().setModelAttributes(this.model);
            this.model.setupAngles(this.getEntityModel());
            IVertexBuilder builder = ItemRenderer.func_229113_a_(renderTypeBuffer, this.model.func_228282_a_(TEXTURE), false, backpackStack.hasEffect());
            this.model.render(stack, builder, p_225628_3_, OverlayTexture.DEFAULT_LIGHT, 1.0F, 2.0F, 2.0F, 2.0F);
            stack.pop();
        }
    }
}
