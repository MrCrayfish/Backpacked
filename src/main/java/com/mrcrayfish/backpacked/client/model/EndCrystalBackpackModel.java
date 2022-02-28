package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class EndCrystalBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/crystal_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 8, -6);

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer crystal;
    private final ModelRenderer frame;
    private final ModelRenderer strap;

    public EndCrystalBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this);
        this.backpack.setPos(0.0F, 24.0F, 0.0F);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(0, 10).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 8.0F, 2.0F, 0.0F, false);
        this.crystal = new ModelRenderer(this);
        this.crystal.setPos(0.0F, 4.0F, 5.25F);
        this.bag.addChild(this.crystal);
        this.crystal.texOffs(14, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, -0.25F, false);
        this.frame = new ModelRenderer(this);
        this.frame.setPos(0.0F, 0.0F, 0.0F);
        this.crystal.addChild(this.frame);
        setRotationAngle(this.frame, -0.7854F, 0.0F, 0.6109F);
        this.frame.texOffs(0, 0).addBox(-2.5F, -2.5F, -2.75F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(0, 20).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(15, 0).addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(0, 20).addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(15, 0).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAngles(@Nullable PlayerEntity player, int animationTick, float partialTick)
    {
        double rotation = animationTick + partialTick;
        this.crystal.y = 4.0F;
        this.crystal.y += Math.sin(rotation / 4.0);
        this.crystal.yRot = (float) Math.toRadians(rotation * 3);
    }

    @Override
    protected ModelRenderer getRoot()
    {
        return this.backpack;
    }

    @Override
    public ModelRenderer getBag()
    {
        return this.bag;
    }

    @Override
    public ModelRenderer getStraps()
    {
        return this.strap;
    }

    @Override
    public ResourceLocation getTextureLocation()
    {
        return TEXTURE;
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
