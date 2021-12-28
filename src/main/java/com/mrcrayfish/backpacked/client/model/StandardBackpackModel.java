package com.mrcrayfish.backpacked.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Author: MrCrayfish
 */
public class StandardBackpackModel extends BackpackModel
{
    private final ModelRenderer root;
    private final ModelRenderer bag;
    private final ModelRenderer straps;

    public StandardBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.root = new ModelRenderer(this);

        this.bag = new ModelRenderer(this);
        this.root.addChild(this.bag);
        this.bag.texOffs(0, 9).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 4.0F, 4.0F, 0.0F, false);
        this.bag.texOffs(22, 0).addBox(3.4239F, 3.6173F, 0.016F, 1.0F, 5.0F, 4.0F, 0.0F, true);
        this.bag.texOffs(0, 0).addBox(-3.5F, 4.0F, 0.0F, 7.0F, 5.0F, 4.0F, 0.0F, false);
        this.bag.texOffs(10, 20).addBox(-3.5F, 4.568F, 4.0F, 7.0F, 3.0F, 1.0F, 0.0F, false);
        this.bag.texOffs(22, 0).addBox(-4.4239F, 3.6173F, 0.016F, 1.0F, 5.0F, 4.0F, 0.0F, false);
        this.bag.texOffs(18, 0).addBox(-1.0F, 1.5F, 2.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.bag.texOffs(16, 30).addBox(-1.5F, -1.0F, 1.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        this.straps = new ModelRenderer(this);
        this.straps.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.straps);
        this.straps.texOffs(0, 17).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.straps.texOffs(22, 21).addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.straps.texOffs(0, 17).addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.straps.texOffs(22, 21).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        ModelRenderer part1 = new ModelRenderer(this);
        part1.setPos(-4.4239F, 8.6173F, 0.016F);
        this.bag.addChild(part1);
        setRotationAngle(part1, 0.0F, 0.0F, -1.1781F);
        part1.texOffs(22, 9).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);

        ModelRenderer part2 = new ModelRenderer(this);
        part2.setPos(-4.4239F, 3.6173F, 0.016F);
        this.bag.addChild(part2);
        setRotationAngle(part2, 0.0F, 0.0F, 1.1781F);
        part2.texOffs(22, 14).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);

        ModelRenderer part3 = new ModelRenderer(this);
        part3.setPos(0.0F, 7.568F, 5.0F);
        this.bag.addChild(part3);
        setRotationAngle(part3, 0.5236F, 0.0F, 0.0F);
        part3.texOffs(6, 17).addBox(-3.5F, -1.0F, -2.0F, 7.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer part4 = new ModelRenderer(this);
        part4.setPos(0.0F, 4.568F, 5.0F);
        this.bag.addChild(part4);
        setRotationAngle(part4, -0.5236F, 0.0F, 0.0F);
        part4.texOffs(6, 17).addBox(-3.5F, 0.0F, -2.0F, 7.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer part5 = new ModelRenderer(this);
        part5.setPos(4.4239F, 8.6173F, 0.016F);
        this.bag.addChild(part5);
        setRotationAngle(part5, 0.0F, 0.0F, 1.1781F);
        part5.texOffs(22, 9).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

        ModelRenderer part6 = new ModelRenderer(this);
        part6.setPos(4.4239F, 3.6173F, 0.016F);
        this.bag.addChild(part6);
        setRotationAngle(part6, 0.0F, 0.0F, -1.1781F);
        part6.texOffs(22, 14).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
    }

    @Override
    protected ModelRenderer getRoot()
    {
        return this.root;
    }

    @Override
    public ModelRenderer getBag()
    {
        return this.bag;
    }

    @Override
    protected ModelRenderer getStraps()
    {
        return this.straps;
    }
}
