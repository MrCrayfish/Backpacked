package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.client.model.backpack.BackpackModel;
import com.mrcrayfish.backpacked.data.pickpocket.PickpocketChallenge;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.npc.AbstractVillager;

/**
 * Author: MrCrayfish
 */
public class VillagerBackpackLayer<T extends AbstractVillager, M extends VillagerModel<T>> extends RenderLayer<T, M>
{
    public VillagerBackpackLayer(RenderLayerParent<T, M> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int p_225628_3_, T villager, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        PickpocketChallenge.get(villager).ifPresent(data ->
        {
            if(data.isBackpackEquipped())
            {
                matrixStack.pushPose();
                BackpackModel model = ModelInstances.get().getWanderingBag();
                ModelPart bag = model.getBag();
                bag.copyFrom(this.getBody(this.getParentModel()));
                bag.z += 3.5F;
                model.getStraps().visible = false;
                VertexConsumer builder = ItemRenderer.getFoilBuffer(buffer, model.renderType(model.getTextureLocation()), false, false);
                bag.render(matrixStack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
                matrixStack.popPose();
            }
        });
    }

    private ModelPart getBody(VillagerModel<T> model)
    {
        return model.root().getChild("body");
    }
}
