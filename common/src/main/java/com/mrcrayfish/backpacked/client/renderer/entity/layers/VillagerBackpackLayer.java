package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class VillagerBackpackLayer extends RenderLayer<VillagerRenderState, VillagerModel>
{
    private static final Identifier WANDERING_BACKPACK = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "wandering_bag");

    private final ItemStack displayStack = new ItemStack(ModItems.BACKPACK.get());
    private final ItemModelResolver itemModelResolver;

    public VillagerBackpackLayer(RenderLayerParent<VillagerRenderState, VillagerModel> renderer, ItemModelResolver itemModelResolver)
    {
        super(renderer);
        this.itemModelResolver = itemModelResolver;
    }

    private ModelPart getBody(VillagerModel model)
    {
        return model.root().getChild("body");
    }

    @Override
    public void submit(PoseStack stack, SubmitNodeCollector collector, int i, VillagerRenderState state, float v, float v1)
    {
        if(!ClientServices.CLIENT.isWearingBackpack(state))
            return;

        // Since wandering traders drink invisibility potion at night, stop drawing the backpack
        if(state.isInvisible)
            return;

        ClientBackpack backpack = ClientRegistry.instance().getBackpack(WANDERING_BACKPACK);
        if(backpack == null)
            return;

        stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        stack.scale(1F, -1F, -1F);
        stack.translate(0, -0.06, 3.5 * 0.0625);

        // TODO 1.21.11 restore
        /*ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(Scene.ON_ENTITY, RenderMode.ALL, stack, source, light, backpack, villager, villager.level(), partialTick, model -> {
                BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
            }, state.ageInTicks);
            stack.pushPose();
            renderer.render(context);
            stack.popPose();
        }, () -> {
            BakedModel model = this.itemRenderer.getItemModelShaper().getModelManager().getModel(backpack.getBaseModel());
            BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
        });*/
        stack.popPose();
    }
}
