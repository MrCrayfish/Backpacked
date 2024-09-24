package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.ModelMeta;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

        CompoundTag tag = stack.getOrCreateTag();
        String modelName = tag.getString("BackpackModel");
        Backpack backpack = BackpackManager.instance().getClientBackpackOrDefault(modelName);
        if(backpack == null)
            return;

        Direction facing = entity.getDirection();
        pose.translate(0.5, 0.0, 0.5);
        pose.translate(0, 0.001, 0);
        pose.mulPose(facing.getRotation());
        pose.translate(-0.5, 0.0, -0.5);
        pose.translate(0.5, -6 * 0.0625, -5 * 0.0625);

        // Apply shelf offset since models can have different shapes and sizes
        ModelMeta meta = BackpackManager.instance().getModelMeta(backpack);
        Vector3f offset = meta.shelfOffset();
        pose.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        // Fix rotation and invert
        pose.mulPose(Axis.XP.rotationDegrees(90F));
        pose.scale(1.0F, -1.0F, -1.0F);

        int animationTick = Optional.ofNullable(Minecraft.getInstance().player).map(player -> player.tickCount).orElse(0);
        meta.renderer().ifPresentOrElse(renderer -> {
            pose.pushPose();
            BackpackRenderContext context = new BackpackRenderContext(pose, buffer, light, stack, backpack, null, partialTick, animationTick, model -> {
                this.itemRenderer.render(stack, ItemDisplayContext.NONE, false, pose, buffer, light, OverlayTexture.NO_OVERLAY, model);
            });
            renderer.forEach(function -> function.apply(context));
            pose.popPose();
        }, () -> {
            BakedModel model = ClientServices.MODEL.getBakedModel(backpack.getBaseModel());
            this.itemRenderer.render(stack, ItemDisplayContext.NONE, false, pose, buffer, light, OverlayTexture.NO_OVERLAY, model);
        });
    }

    private BakedModel getModel(ResourceLocation location)
    {
        BakedModel model = ClientServices.MODEL.getBakedModel(location);
        return model != null ? model : this.missingModel.get();
    }
}
