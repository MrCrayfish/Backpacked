package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity>
{
    private final ItemRenderer itemRenderer;
    private final Supplier<BakedModel> missingModel;

    public ShelfRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemRenderer = context.getItemRenderer();
        this.missingModel = () -> this.itemRenderer.getItemModelShaper().getModelManager().getMissingModel();
    }

    @Override
    public void render(ShelfBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay)
    {
        ItemStack stack = entity.getBackpack();
        if(stack.getItem() != ModItems.BACKPACK.get())
            return;

        BackpackProperties properties = stack.getOrDefault(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT);
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(properties.model());
        if(backpack == null)
            return;

        Direction facing = entity.getDirection();
        pose.translate(0.5, 0.0, 0.5);
        pose.translate(0, 0.001, 0);
        pose.mulPose(facing.getRotation());
        pose.translate(-0.5, 0.0, -0.5);
        pose.translate(0.5, -6 * 0.0625, -5 * 0.0625);

        // Apply shelf offset since models can have different shapes and sizes
        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        Vector3f offset = meta.shelfOffset();
        pose.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        // Fix rotation and invert
        pose.mulPose(Axis.XP.rotationDegrees(90F));
        pose.scale(1.0F, -1.0F, -1.0F);

        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(pose, buffer, light, stack, backpack, null, entity.getLevel(), partialTick, model -> {
                this.itemRenderer.render(stack, ItemDisplayContext.NONE, false, pose, buffer, light, OverlayTexture.NO_OVERLAY, model);
            }, this.itemRenderer);
            pose.pushPose();
            renderer.render(context);
            pose.popPose();
        }, () -> {
            BakedModel model = this.getModel(backpack.getBaseModel());
            this.itemRenderer.render(stack, ItemDisplayContext.NONE, false, pose, buffer, light, OverlayTexture.NO_OVERLAY, model);
        });
    }

    private BakedModel getModel(ModelResourceLocation location)
    {
        return this.itemRenderer.getItemModelShaper().getModelManager().getModel(location);
    }
}
