package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Author: MrCrayfish
 */
public class VillagerBackpackLayer<T extends AbstractVillagerEntity, M extends VillagerModel<T>> extends LayerRenderer<T, M>
{
    public static final Field bodyField = ObfuscationReflectionHelper.findField(VillagerModel.class, "field_78189_b");

    public VillagerBackpackLayer(IEntityRenderer<T, M> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225628_3_, T villager, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        PickpocketChallenge.get(villager).ifPresent(data ->
        {
            if(data.isBackpackEquipped())
            {
                ModelRenderer body = this.getBody(this.getParentModel());
                if(body == null)
                    return;

                matrixStack.pushPose();
                BackpackModel model = ModelInstances.WANDERING_BAG;
                ModelRenderer bag = model.getBag();
                bag.copyFrom(body);
                bag.z += 3.5F;
                model.getStraps().visible = false;
                IVertexBuilder builder = ItemRenderer.getFoilBuffer(buffer, model.renderType(model.getTextureLocation()), false, false);
                bag.render(matrixStack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
                matrixStack.popPose();
            }
        });
    }

    @Nullable
    private ModelRenderer getBody(VillagerModel<T> model)
    {
        try
        {
            return (ModelRenderer) bodyField.get(model);
        }
        catch(IllegalAccessException ignored)
        {
            return null;
        }
    }
}
