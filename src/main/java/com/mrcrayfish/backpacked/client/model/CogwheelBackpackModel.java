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
public class CogwheelBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/cogwheel_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 9, -7);

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer gear;
    private final ModelRenderer strap;

    public CogwheelBackpackModel()
    {
        this.texWidth = 64;
        this.texHeight = 64;
        this.backpack = new ModelRenderer(this);
        this.backpack.setPos(0.0F, 24.0F, 0.0F);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(14, 14).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 6.0F, 0.0F, false);
        this.bag.texOffs(14, 14).addBox(-4.0F, 8.0F, 0.0F, 8.0F, 1.0F, 6.0F, 0.0F, false);
        this.bag.texOffs(0, 0).addBox(-4.0F, 0.5F, 0.0F, 8.0F, 8.0F, 6.0F, -0.25F, false);
        this.gear = new ModelRenderer(this);
        this.gear.setPos(0.0F, 4.5F, 0.0F);
        this.bag.addChild(this.gear);
        this.gear.texOffs(0, 14).addBox(-1.0F, -1.0F, -0.25F, 2.0F, 2.0F, 10.0F, -0.25F, false);
        this.gear.texOffs(30, 21).addBox(-2.0F, -2.0F, 6.5F, 4.0F, 4.0F, 2.0F, 0.0F, false);
        this.gear.texOffs(0, 26).addBox(-3.0F, -3.0F, 7.0F, 6.0F, 6.0F, 1.0F, -0.125F, false);
        this.gear.texOffs(28, 0).addBox(-1.0F, -5.0F, 6.5F, 2.0F, 10.0F, 2.0F, -0.25F, false);
        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 7.0F);
        this.gear.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.texOffs(28, 0).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 10.0F, 2.0F, -0.25F, false);
        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 0.0F, 7.0F);
        this.gear.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.7854F);
        cube_r2.texOffs(28, 0).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 10.0F, 2.0F, -0.25F, false);
        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, 0.0F, 7.0F);
        this.gear.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, -1.5708F);
        cube_r3.texOffs(28, 0).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 10.0F, 2.0F, -0.25F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(20, 22).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(0, 14).addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(20, 22).addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(0, 14).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAngles(@Nullable PlayerEntity player, int animationTick, float partialTick)
    {
        float position = player != null ? player.animationPosition : 0F;
        float speed = player != null ? player.animationSpeed : 0F;
        this.gear.zRot = (float) Math.toRadians((animationTick + partialTick) * 4.0F + (position - speed * (1.0F - partialTick)) * 16.0F);
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
