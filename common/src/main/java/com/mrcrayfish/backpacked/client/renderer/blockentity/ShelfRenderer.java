package com.mrcrayfish.backpacked.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.Icons;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.client.renderer.blockentity.state.ShelfRenderState;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity, ShelfRenderState>
{
    private static final Component RECALL_ICON = ScreenUtil.getIconComponent(Icons.RECALL);

    private final ItemModelResolver itemModelResolver;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ShelfRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemModelResolver = context.itemModelResolver();
        this.entityRenderDispatcher = context.entityRenderer();
    }

    @Override
    public ShelfRenderState createRenderState()
    {
        return new ShelfRenderState();
    }

    @Override
    public void extractRenderState(ShelfBlockEntity entity, ShelfRenderState state, float partialTick, Vec3 camera, ModelFeatureRenderer.@Nullable CrumblingOverlay overlay)
    {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        state.direction = entity.getDirection();
        state.recallQueueCount = entity.getRecallQueueCount();

        ItemStack stack = entity.getBackpack();
        state.hasBackpack = !stack.isEmpty();
        if(stack.has(DataComponents.CUSTOM_NAME))
        {
            Component label = stack.get(DataComponents.CUSTOM_NAME);
            if(label != null)
            {
                state.nameplate = label;
            }
        }

        CosmeticProperties properties = stack.getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
        Identifier modelId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(modelId);
        if(backpack != null)
        {
            ModelMeta meta = backpack.getModelMeta();
            state.shelfOffset = meta.shelfOffset();
            state.baseModel = backpack.getBaseModel();
            state.strapsModel = backpack.getStrapsModel();
        }

        state.partialTick = partialTick;
        state.playingAnimation = entity.isAnimationPlaying();
        state.animationTicks = entity.getAnimationTicks();
        state.renderTicks = entity.getRenderTicks();
    }

    @Override
    public void submit(ShelfRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera)
    {
        if(!state.hasBackpack)
            return;

        Direction facing = state.direction;
        this.renderBackpackName(state, facing, pose, collector, camera);

        pose.pushPose();

        pose.translate(0.5, 0.0, 0.5);
        pose.translate(0, 0.001, 0);
        pose.mulPose(facing.getRotation());

        pose.translate(-0.5, 0.0, -0.5);
        pose.translate(0.5, -6 * 0.0625, -5 * 0.0625);

        if(state.playingAnimation)
        {
            state.applyAnimation(0, 1, time -> {
                pose.translate(0, 0, -0.5 * (1 - time));
            });
            state.applyAnimation(0, 1, time -> {
                float scale = 0.25F + 0.75F * time;
                pose.translate(0, 3 * 0.0625, 8 * 0.0625);
                pose.scale(scale, scale, scale);
                pose.translate(0, -3 * 0.0625, -8 * 0.0625);
            });
            state.applyAnimation(1, 4, time -> {
                float stretch = Mth.sin(Mth.PI * time) * 0.25F;
                float flatten = Mth.sin(Mth.PI * time) * 0.15F * -1;
                pose.translate(0, 3 * 0.0625, 8 * 0.0625);
                pose.scale(1 + stretch, 1 + stretch, 1 + flatten);
                pose.translate(0, -3 * 0.0625, -8 * 0.0625);
            });
        }

        // Apply shelf offset since models can have different shapes and sizes
        Vector3fc offset = state.shelfOffset;
        if(offset != null)
        {
            pose.translate(offset.x() * 0.0625, offset.z() * 0.0625, -offset.y() * 0.0625);
        }

        // Fix rotation and invert
        pose.mulPose(Axis.XP.rotationDegrees(90F));
        pose.scale(1.0F, -1.0F, -1.0F);

        BackpackRenderer renderer = state.renderer;
        if(renderer != null)
        {
            BackpackRenderContext context = new BackpackRenderContext(Scene.ON_SHELF, RenderMode.MODELS_ONLY, pose, state.baseModel, state.strapsModel, null, null, 0, state.lightCoords, state.renderTicks, state.partialTick, model -> {
                pose.pushPose();
                pose.translate(-0.5F, -0.5F, -0.5F);
                StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, state.lightCoords, OverlayTexture.NO_OVERLAY);
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
                    StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    pose.popPose();
                }
            }
        }

        pose.popPose();
    }

    private void renderBackpackName(ShelfRenderState state, Direction facing, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera)
    {
        if(state.nameplate == null)
            return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(facing.getRotation());
        poseStack.translate(0, -0.1875, -1.1875);
        poseStack.mulPose(facing.getRotation().invert());
        poseStack.mulPose(this.entityRenderDispatcher.camera.rotation());
        poseStack.scale(0.02F, -0.02F, 0.02F);

        Minecraft mc = Minecraft.getInstance();
        if(mc.hitResult instanceof BlockHitResult result && result.getBlockPos().equals(state.blockPos))
        {
            collector.submitNameTag(poseStack, null, 0, state.nameplate, true, state.lightCoords, 0, camera);
            /*float halfWidth = mc.font.width(label) / 2F;
            mc.font.drawInBatch(label, -halfWidth, 0, 0x20FFFFFF, false, poseStack.last().pose(), source, Font.DisplayMode.SEE_THROUGH, 0x2A000000, light);
            mc.font.drawInBatch(label, -halfWidth, 0, -1, true, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
            poseStack.translate(0, -12, 0);*/
        }

        int recallCount = state.recallQueueCount;
        if(recallCount > 0)
        {
            poseStack.scale(1.1F, 1.1F, 1.1F);
            Component label = ScreenUtil.join(" ", RECALL_ICON, Component.literal(Integer.toString(recallCount)));
            collector.submitNameTag(poseStack, null, 0, label, true, state.lightCoords, 0, camera);
            /*float halfWidth = mc.font.width(label) / 2F;
            mc.font.drawInBatch(label, -halfWidth, 0, 0x20FFFFFF, false, matrix, source, Font.DisplayMode.SEE_THROUGH, 0, light);
            mc.font.drawInBatch(label, -halfWidth, 0, -1, false, matrix, source, Font.DisplayMode.NORMAL, 0, light);*/
        }

        poseStack.popPose();
    }
}
