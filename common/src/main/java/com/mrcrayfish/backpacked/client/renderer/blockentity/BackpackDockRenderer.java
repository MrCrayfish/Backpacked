package com.mrcrayfish.backpacked.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.BackpackDockBlockEntity;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.client.renderer.blockentity.state.BackpackDockRenderState;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class BackpackDockRenderer implements BlockEntityRenderer<BackpackDockBlockEntity, BackpackDockRenderState>
{
    public BackpackDockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public BackpackDockRenderState createRenderState()
    {
        return new BackpackDockRenderState();
    }

    @Override
    public void extractRenderState(BackpackDockBlockEntity entity, BackpackDockRenderState state, float partialTick, Vec3 camera, ModelFeatureRenderer.@Nullable CrumblingOverlay overlay)
    {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        state.direction = entity.getDirection();
        state.hasBackpack = entity.hasBackpack();
        state.partialTick = partialTick;

        CosmeticProperties properties = entity.getBackpack().getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
        Identifier modelId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(modelId);
        if(backpack != null)
        {
            ModelMeta meta = backpack.getModelMeta();
            state.renderer = meta.renderer().orElse(null);
            state.shelfOffset = meta.shelfOffset();
            state.baseModel = backpack.getBaseModel();
            state.strapsModel = backpack.getStrapsModel();
        }

        Level level = entity.getLevel();
        if(level != null)
        {
            BlockPos lightPos = entity.getBlockPos().relative(entity.getDirection());
            state.itemLight = LightCoordsUtil.getLightCoords(level, lightPos);
        }

        // Easy solution could use a tick count on block entity in the future
        Minecraft mc = Minecraft.getInstance();
        state.tickCount = mc.player != null ? mc.player.tickCount : 0;
    }

    @Override
    public void submit(BackpackDockRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState cameraRenderState)
    {
        if(!state.hasBackpack)
            return;

        pose.translate(0.5, 0.0, 0.5);
        pose.translate(0, 0.001, 0);
        pose.mulPose(state.direction.getRotation());
        pose.translate(-0.5, 0.0, -0.5);

        Vector3fc offset = state.shelfOffset;
        pose.translate(offset.x() * 0.0625, offset.z() * 0.0625, -offset.y() * 0.0625);

        pose.translate(0.5, 3 * 0.0625, -3 * 0.0625);
        pose.mulPose(Axis.XP.rotationDegrees(-90F));

        BackpackRenderer renderer = state.renderer;
        if(renderer != null)
        {
            BackpackRenderContext context = new BackpackRenderContext(Scene.ON_SHELF, RenderMode.MODELS_ONLY, pose, state.baseModel, state.strapsModel, null, null, 0, state.itemLight, state.tickCount, state.partialTick, model -> {
                pose.pushPose();
                pose.translate(-0.5F, -0.5F, -0.5F);
                StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, state.itemLight, OverlayTexture.NO_OVERLAY);
                pose.popPose();
            });
            renderer.render(context);
        }
        else
        {
            FrameworkModelResource<FrameworkBakedModel> resource = StandaloneModels.getResource(state.baseModel);
            if(resource != null)
            {
                FrameworkBakedModel model = resource.getModel();
                if(model != null)
                {
                    pose.pushPose();
                    pose.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, state.itemLight, OverlayTexture.NO_OVERLAY);
                    pose.popPose();
                }
            }
        }
    }
}
