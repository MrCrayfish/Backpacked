package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.tileentity.ShelfTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ShelfRenderer extends TileEntityRenderer<ShelfTileEntity>
{
    public ShelfRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(ShelfTileEntity entity, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay)
    {
        ItemStack stack = entity.getBackpack();
        if(stack.getItem() != ModItems.BACKPACK.get())
            return;

        CompoundNBT tag = stack.getOrCreateTag();
        String modelName = tag.getString("BackpackModel");
        BackpackModel model = BackpackLayer.getModel(modelName);

        Direction facing = entity.getDirection();
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.translate(0, 5 * 0.0625, 0);
        matrixStack.translate(0, 0.001, 0);
        matrixStack.mulPose(facing.getRotation());

        Vector3d offset = model.getShelfOffset();
        matrixStack.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90F));

        int animationTick = Optional.ofNullable(Minecraft.getInstance().player).map(player -> player.tickCount).orElse(0);
        IVertexBuilder builder = buffer.getBuffer(model.renderType(model.getTextureLocation()));
        model.setupAngles(null, animationTick, partialTick);
        model.getStraps().visible = false;
        model.getBag().setPos(0F, 0F, 0F);
        model.getBag().render(matrixStack, builder, light, overlay);
    }
}
