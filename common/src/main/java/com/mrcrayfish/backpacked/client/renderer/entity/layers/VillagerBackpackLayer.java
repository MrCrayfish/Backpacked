package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class VillagerBackpackLayer extends RenderLayer<VillagerRenderState, VillagerModel>
{
    public static final Identifier WANDERING_BACKPACK = Utils.id("wandering_bag");

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
    public void submit(PoseStack pose, SubmitNodeCollector collector, int i, VillagerRenderState state, float v, float v1)
    {
        // Since wandering traders drink invisibility potion at night, stop drawing the backpack
        if(state.isInvisible)
            return;

        BackpackRenderState backpackRenderState = ClientServices.CLIENT.getBackpackRenderState(state);
        if(backpackRenderState == null)
            return;

        ClientBackpack backpack = ClientRegistry.instance().getBackpack(WANDERING_BACKPACK);
        if(backpack == null)
            return;

        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        pose.scale(1F, -1F, -1F);
        pose.translate(0, -0.06, 3.5 * 0.0625);

        BackpackRenderer renderer = backpackRenderState.renderer;
        if(renderer != null)
        {
            BackpackRenderContext context = new BackpackRenderContext(
                Scene.ON_ENTITY,
                RenderMode.ALL,
                pose,
                backpackRenderState.baseModel,
                backpackRenderState.strapsModel,
                backpackRenderState.entityData,
                backpackRenderState.levelData,
                0, // Don't care about entity id.
                state.lightCoords,
                (int) state.ageInTicks,
                state.ageInTicks - (int) state.ageInTicks,
                model -> {
                    pose.pushPose();
                    pose.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    pose.popPose();
                });
            renderer.render(context);
        }
        else
        {
            FrameworkModelResource<FrameworkBakedModel> resource = StandaloneModels.getResource(backpackRenderState.baseModel);
            if(resource != null)
            {
                FrameworkBakedModel model = resource.getModel();
                if(model != null)
                {
                    pose.pushPose();
                    pose.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    pose.popPose();
                }
            }
        }
        pose.popPose();
    }
}
