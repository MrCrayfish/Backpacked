package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import com.mrcrayfish.backpacked.integration.Curios;
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
    public void render(MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        ItemStack backpackStack = Backpacked.getBackpackStack(player);
        if(!backpackStack.isEmpty())
        {
            ItemStack chestStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if(chestStack.getItem() == Items.ELYTRA)
            {
                return;
            }

            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
            {
                return;
            }

            stack.push();
            this.getEntityModel().setModelAttributes(this.model);
            this.model.setupAngles(this.getEntityModel());
            IVertexBuilder builder = ItemRenderer.func_239391_c_(renderTypeBuffer, this.model.getRenderType(TEXTURE), false, backpackStack.hasEffect());
            this.model.render(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
            stack.pop();
        }
    }
}
