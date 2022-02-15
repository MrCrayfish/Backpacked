package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.tileentity.ShelfBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity>
{
    public ShelfRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ShelfBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        ItemStack stack = entity.getBackpack();
        if(stack.getItem() != ModItems.BACKPACK.get())
            return;

        CompoundTag tag = stack.getOrCreateTag();
        String modelName = tag.getString("BackpackModel");
        BackpackModel model = BackpackLayer.getModel(modelName).get();

        Direction facing = entity.getDirection();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.translate(0, 5 * 0.0625, 0);
        poseStack.translate(0, 0.001, 0);
        poseStack.mulPose(facing.getRotation());

        Vector3d offset = model.getShelfOffset();
        poseStack.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90F));

        int animationTick = Optional.ofNullable(Minecraft.getInstance().player).map(player -> player.tickCount).orElse(0);
        VertexConsumer builder = buffer.getBuffer(model.renderType(model.getTextureLocation()));
        model.setupAngles(null, animationTick, partialTick);
        model.getStraps().visible = false;
        model.getBag().setPos(0F, 0F, 0F);
        model.getBag().render(poseStack, builder, light, overlay);
    }
}
