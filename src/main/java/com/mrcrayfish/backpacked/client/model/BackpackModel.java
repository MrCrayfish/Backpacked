package com.mrcrayfish.backpacked.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public abstract class BackpackModel extends Model
{
    public BackpackModel()
    {
        super(RenderType::entityCutoutNoCull);
    }

    protected static void setRotationAngle(ModelRenderer renderer, float x, float y, float z)
    {
        renderer.xRot = x;
        renderer.yRot = y;
        renderer.zRot = z;
    }

    public void setupAngles(ModelRenderer body, boolean armour)
    {
        ModelRenderer root = this.getRoot();
        root.copyFrom(body);
        root.y += 8.9;
        root.z += 2;

        ModelRenderer straps = this.getStraps();
        straps.visible = !armour;
        if(armour)
        {
            root.z += 1;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        matrixStack.scale(1.05F, 1.05F, 1.05F);
        this.getRoot().render(matrixStack, builder, p_225598_3_, p_225598_4_);
    }

    protected abstract ModelRenderer getRoot();

    protected abstract ModelRenderer getStraps();
}
