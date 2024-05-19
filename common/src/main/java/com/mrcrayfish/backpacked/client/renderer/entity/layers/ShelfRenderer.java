package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.model.backpack.BackpackModel;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity>
{
    private final ItemRenderer renderer;
    private final Supplier<BakedModel> missingModel;

    public ShelfRenderer(BlockEntityRendererProvider.Context context)
    {
        this.renderer = context.getItemRenderer();
        this.missingModel = () -> this.renderer.getItemModelShaper().getModelManager().getMissingModel();
    }

    @Override
    public void render(ShelfBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay)
    {
        ItemStack stack = entity.getBackpack();
        if(stack.getItem() != ModItems.BACKPACK.get())
            return;

        CompoundTag tag = stack.getOrCreateTag();
        String modelName = tag.getString("BackpackModel");
        Backpack backpack = BackpackManager.instance().getBackpack(modelName);
        if(backpack == null) // TODO render default
            return;

        BakedModel model = backpack.getModel()
            .map(ClientServices.MODEL::getBakedModel)
            .orElse(this.missingModel.get());

        Direction facing = entity.getDirection();
        pose.translate(0.5, 0.0, 0.5);
        //pose.translate(0, 10 * 0.0625, 0);
        //pose.translate(0, 0.001, 0);
        pose.mulPose(facing.getRotation());
        pose.translate(-0.5, 0.0, -0.5);
        pose.translate(0.5, -6 * 0.0625, -5 * 0.0625);

        Vector3f offset = backpack.getModelMeta().getShelfOffset();
        pose.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        pose.mulPose(Axis.XP.rotationDegrees(90F));
        pose.scale(1.0F, -1.0F, -1.0F);

        int animationTick = Optional.ofNullable(Minecraft.getInstance().player).map(player -> player.tickCount).orElse(0);
        this.renderer.render(stack, ItemDisplayContext.NONE, false, pose, buffer, light, overlay, model);
        /*VertexConsumer builder = buffer.getBuffer(model.renderType(model.getTextureLocation()));
        model.setupAngles(null, animationTick, partialTick);
        model.getStraps().visible = false;
        model.getBag().setPos(0F, 0F, 0F);
        model.getBag().render(pose, builder, light, overlay);*/
    }
}
