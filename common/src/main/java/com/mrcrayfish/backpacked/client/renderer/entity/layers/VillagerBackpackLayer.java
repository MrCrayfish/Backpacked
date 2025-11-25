package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.BakedModelRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
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
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class VillagerBackpackLayer<T extends AbstractVillager, M extends VillagerModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation WANDERING_BACKPACK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "wandering_bag");

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

            ClientBackpack backpack = ClientRegistry.instance().getBackpack(WANDERING_BACKPACK);
            if(backpack == null)
                return;

            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.scale(1F, -1F, -1F);
            pose.translate(0, -0.06, 3.5 * 0.0625);

            ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
            meta.renderer().ifPresentOrElse(renderer -> {
                BackpackRenderContext context = new BackpackRenderContext(Scene.ON_ENTITY, RenderMode.ALL, pose, source, light, backpack, villager, villager.level(), partialTick, model -> {
                    BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
                }, villager.tickCount);
                pose.pushPose();
                renderer.render(context);
                pose.popPose();
            }, () -> {
                BakedModel model = this.itemRenderer.getItemModelShaper().getModelManager().getModel(backpack.getBaseModel());
                BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
            });
            pose.popPose();
        });
    }

    private ModelPart getBody(VillagerModel<T> model)
    {
        return model.root().getChild("body");
    }
}
