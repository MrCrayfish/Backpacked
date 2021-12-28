package com.mrcrayfish.backpacked.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Author: MrCrayfish
 */
public class StandardBackpackModel extends BackpackModel
{
    public final ModelRenderer root;
    public final ModelRenderer backpack;
    public final ModelRenderer right2;
    public final ModelRenderer right1;
    public final ModelRenderer front2;
    public final ModelRenderer front1;
    public final ModelRenderer left2;
    public final ModelRenderer left1;
    public final ModelRenderer straps;

    public StandardBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.root = new ModelRenderer(this);
        this.root.setPos(0.0F, 24.0F, 0.0F);
        this.backpack = new ModelRenderer(this);
        this.backpack.setPos(-4.4239F, -0.3827F, 0.016F);
        this.root.addChild(this.backpack);
        this.backpack.texOffs(0, 9).addBox(0.9239F, -8.6173F, -0.016F, 7.0F, 4.0F, 4.0F, 0.0F, false);
        this.backpack.texOffs(22, 0).addBox(7.8478F, -5.0F, 0.0F, 1.0F, 5.0F, 4.0F, 0.0F, true);
        this.backpack.texOffs(0, 0).addBox(0.9239F, -4.6173F, -0.016F, 7.0F, 5.0F, 4.0F, 0.0F, false);
        this.backpack.texOffs(10, 20).addBox(0.9239F, -4.0493F, 3.984F, 7.0F, 3.0F, 1.0F, 0.0F, false);
        this.backpack.texOffs(22, 0).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 4.0F, 0.0F, false);
        this.backpack.texOffs(18, 0).addBox(3.4239F, -7.1173F, 2.484F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.backpack.texOffs(16, 30).addBox(1.5F, -9.0F, 1.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        this.right2 = new ModelRenderer(this);
        this.right2.setPos(0.0F, 0.0F, 0.0F);
        this.backpack.addChild(this.right2);
        setRotationAngle(this.right2, 0.0F, 0.0F, -1.1781F);
        this.right2.texOffs(22, 9).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.right1 = new ModelRenderer(this);
        this.right1.setPos(0.0F, -5.0F, 0.0F);
        this.backpack.addChild(this.right1);
        setRotationAngle(this.right1, 0.0F, 0.0F, 1.1781F);
        this.right1.texOffs(22, 14).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.front2 = new ModelRenderer(this);
        this.front2.setPos(4.4239F, -1.0493F, 4.984F);
        this.backpack.addChild(this.front2);
        setRotationAngle(this.front2, 0.5236F, 0.0F, 0.0F);
        this.front2.texOffs(6, 17).addBox(-3.5F, -1.0F, -2.0F, 7.0F, 1.0F, 2.0F, 0.0F, false);
        this.front1 = new ModelRenderer(this);
        this.front1.setPos(4.4239F, -4.0493F, 4.984F);
        this.backpack.addChild(this.front1);
        setRotationAngle(this.front1, -0.5236F, 0.0F, 0.0F);
        this.front1.texOffs(6, 17).addBox(-3.5F, 0.0F, -2.0F, 7.0F, 1.0F, 2.0F, 0.0F, false);
        this.left2 = new ModelRenderer(this);
        this.left2.setPos(8.8478F, 0.0F, 0.0F);
        this.backpack.addChild(this.left2);
        setRotationAngle(this.left2, 0.0F, 0.0F, 1.1781F);
        this.left2.texOffs(22, 9).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        this.left1 = new ModelRenderer(this);
        this.left1.setPos(8.8478F, -5.0F, 0.0F);
        this.backpack.addChild(this.left1);
        setRotationAngle(this.left1, 0.0F, 0.0F, -1.1781F);
        this.left1.texOffs(22, 14).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        this.straps = new ModelRenderer(this);
        this.straps.setPos(-3.0F, -1.0F, 0.0F);
        this.root.addChild(this.straps);
        this.straps.texOffs(0, 17).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.straps.texOffs(22, 21).addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.straps.texOffs(0, 17).addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.straps.texOffs(22, 21).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
    }

    @Override
    protected ModelRenderer getRoot()
    {
        return this.root;
    }

    @Override
    protected ModelRenderer getStraps()
    {
        return this.straps;
    }
}
