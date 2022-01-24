package com.mrcrayfish.backpacked.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class BackpackModel extends Model
{
    private static final Vector3d DEFAULT_OFFSET = new Vector3d(0, 9, -6);

    public BackpackModel()
    {
        super(RenderType::entityCutoutNoCull);
    }

    public BackpackModel(Function<ResourceLocation, RenderType> renderType)
    {
        super(renderType);
    }

    protected static void setRotationAngle(ModelRenderer renderer, float x, float y, float z)
    {
        renderer.xRot = x;
        renderer.yRot = y;
        renderer.zRot = z;
    }

    public void setupAngles(@Nullable PlayerEntity player, int animationTick, float partialTick) {}

    public void transformToPlayerBody(ModelRenderer body, boolean armour)
    {
        ModelRenderer root = this.getRoot();
        root.copyFrom(body);

        ModelRenderer bag = this.getBag();
        bag.setPos(0.0F, -0.2F, 2.0F + (armour ? 1.0F : 0.0F));

        ModelRenderer straps = this.getStraps();
        straps.visible = !armour;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        matrixStack.scale(1.05F, 1.05F, 1.05F);
        this.getRoot().render(matrixStack, builder, p_225598_3_, p_225598_4_);
    }

    public void tickForPlayer(Vector3d pos, PlayerEntity player) {}

    protected abstract ModelRenderer getRoot();

    public abstract ModelRenderer getBag();

    public abstract ModelRenderer getStraps();

    public abstract ResourceLocation getTextureLocation();

    public Vector3d getShelfOffset()
    {
        return DEFAULT_OFFSET;
    }
}
