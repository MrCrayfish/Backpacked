package com.mrcrayfish.backpacked.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.Icons;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.BakedModelRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Author: MrCrayfish
 */
public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity>
{
    private static final Component RECALL_ICON = ScreenUtil.getIconComponent(Icons.RECALL);

    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ShelfRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemRenderer = context.getItemRenderer();
        this.entityRenderDispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(ShelfBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay)
    {
        ItemStack stack = entity.getBackpack();
        if(!stack.is(ModItems.BACKPACK.get()))
            return;

        CosmeticProperties properties = stack.getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
        ResourceLocation modelId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(modelId);
        if(backpack == null)
            return;

        Direction facing = entity.getDirection();
        this.renderBackpackName(entity, facing, pose, buffer, light);

        pose.translate(0.5, 0.0, 0.5);
        pose.translate(0, 0.001, 0);
        pose.mulPose(facing.getRotation());

        pose.translate(-0.5, 0.0, -0.5);
        pose.translate(0.5, -6 * 0.0625, -5 * 0.0625);

        if(entity.isAnimationPlaying())
        {
            entity.applyAnimation(0, 1, partialTick, time -> {
                pose.translate(0, 0, -0.5 * (1 - time));
            });
            entity.applyAnimation(0, 1, partialTick, time -> {
                float scale = 0.25F + 0.75F * time;
                pose.translate(0, 3 * 0.0625, 8 * 0.0625);
                pose.scale(scale, scale, scale);
                pose.translate(0, -3 * 0.0625, -8 * 0.0625);
            });
            entity.applyAnimation(1, 4, partialTick, time -> {
                float stretch = Mth.sin(Mth.PI * time) * 0.25F;
                float flatten = Mth.sin(Mth.PI * time) * 0.15F * -1;
                pose.translate(0, 3 * 0.0625, 8 * 0.0625);
                pose.scale(1 + stretch, 1 + stretch, 1 + flatten);
                pose.translate(0, -3 * 0.0625, -8 * 0.0625);
            });
        }

        // Apply shelf offset since models can have different shapes and sizes
        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        Vector3f offset = meta.shelfOffset();
        pose.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        // Fix rotation and invert
        pose.mulPose(Axis.XP.rotationDegrees(90F));
        pose.scale(1.0F, -1.0F, -1.0F);

        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(Scene.ON_SHELF, RenderMode.MODELS_ONLY, pose, buffer, light, backpack, null, entity.getLevel(), partialTick, model -> {
                BakedModelRenderer.drawBakedModel(model, pose, buffer, light, OverlayTexture.NO_OVERLAY);
            }, entity.tickCount);
            pose.pushPose();
            renderer.render(context);
            pose.popPose();
        }, () -> {
            BakedModel model = this.getModel(backpack.getBaseModel());
            BakedModelRenderer.drawBakedModel(model, pose, buffer, light, OverlayTexture.NO_OVERLAY);
        });
        RenderSystem.disableBlend();
    }

    private BakedModel getModel(ModelResourceLocation location)
    {
        return this.itemRenderer.getItemModelShaper().getModelManager().getModel(location);
    }

    private void renderBackpackName(ShelfBlockEntity shelf, Direction facing, PoseStack poseStack, MultiBufferSource source, int light)
    {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(facing.getRotation());
        poseStack.translate(0, -0.1875, -1.1875);
        poseStack.mulPose(facing.getRotation().invert());
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.02F, -0.02F, 0.02F);

        Minecraft mc = Minecraft.getInstance();
        if(mc.hitResult instanceof BlockHitResult result && result.getBlockPos().equals(shelf.getBlockPos()))
        {
            ItemStack backpack = shelf.getBackpack();
            if(!backpack.isEmpty() && backpack.has(DataComponents.CUSTOM_NAME))
            {
                Component label = backpack.get(DataComponents.CUSTOM_NAME);
                if(label != null)
                {
                    float halfWidth = mc.font.width(label) / 2F;
                    mc.font.drawInBatch(label, -halfWidth, 0, 0x20FFFFFF, false, poseStack.last().pose(), source, Font.DisplayMode.SEE_THROUGH, 0x2A000000, light);
                    mc.font.drawInBatch(label, -halfWidth, 0, -1, true, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
                    poseStack.translate(0, -12, 0);
                }
            }
        }

        int recallCount = shelf.getRecallQueueCount();
        if(recallCount > 0)
        {
            poseStack.scale(1.1F, 1.1F, 1.1F);
            Matrix4f matrix = poseStack.last().pose();
            Component label = ScreenUtil.join(" ", RECALL_ICON, Component.literal(Integer.toString(recallCount)));
            float halfWidth = mc.font.width(label) / 2F;
            mc.font.drawInBatch(label, -halfWidth, 0, 0x20FFFFFF, false, matrix, source, Font.DisplayMode.SEE_THROUGH, 0, light);
            mc.font.drawInBatch(label, -halfWidth, 0, -1, false, matrix, source, Font.DisplayMode.NORMAL, 0, light);
        }

        poseStack.popPose();
    }
}
