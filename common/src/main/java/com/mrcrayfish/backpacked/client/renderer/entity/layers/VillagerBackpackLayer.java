package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.ModelMeta;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class VillagerBackpackLayer<T extends AbstractVillager, M extends VillagerModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation WANDERING_BACKPACK = new ResourceLocation(Constants.MOD_ID, "wandering_bag");

    private final ItemStack displayStack = new ItemStack(ModItems.BACKPACK.get());
    private final ItemRenderer itemRenderer;

    public VillagerBackpackLayer(RenderLayerParent<T, M> renderer, ItemRenderer itemRenderer)
    {
        super(renderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int light, T villager, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        TraderPickpocketing.get(villager).ifPresent(data ->
        {
            if(!data.isBackpackEquipped())
                return;

            // Since wandering traders drink invisibility potion at night, stop drawing the backpack
            if(villager.isInvisible())
                return;

            Backpack backpack = BackpackManager.instance().getClientBackpack(WANDERING_BACKPACK);
            if(backpack == null)
                return;

            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.scale(1F, -1F, -1F);
            pose.translate(0, -0.06, 3.5 * 0.0625);

            ModelMeta meta = BackpackManager.instance().getModelMeta(backpack);
            meta.renderer().ifPresentOrElse(renderer -> {
                pose.pushPose();
                BackpackRenderContext context = new BackpackRenderContext(pose, source, light, this.displayStack, backpack, villager, partialTick, villager.tickCount, model -> {
                    this.itemRenderer.render(this.displayStack, ItemDisplayContext.NONE, false, pose, source, light, OverlayTexture.NO_OVERLAY, model);
                });
                renderer.forEach(function -> function.apply(context));
                pose.popPose();
            }, () -> {
                BakedModel model = ClientServices.MODEL.getBakedModel(backpack.getBaseModel());
                this.itemRenderer.render(this.displayStack, ItemDisplayContext.NONE, false, pose, source, light, OverlayTexture.NO_OVERLAY, model);
            });
            pose.popPose();
        });
    }

    private ModelPart getBody(VillagerModel<T> model)
    {
        return model.root().getChild("body");
    }
}
