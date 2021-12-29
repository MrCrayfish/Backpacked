package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class BambooBasketBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/bamboo_basket_backpack.png");

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer strap;

    public BambooBasketBackpackModel()
    {
        this.texWidth = 64;
        this.texHeight = 64;
        this.backpack = new ModelRenderer(this);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(0, 0).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 9.0F, 7.0F, 0.0F, false);
        this.bag.texOffs(0, 16).addBox(-4.0F, -0.25F, -0.5F, 8.0F, 1.0F, 8.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(24, 12).addBox(5.0F, -8.0F, -4.0F, 1.0F, 6.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(0, 25).addBox(6.0F, -3.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(24, 12).addBox(0.0F, -8.0F, -4.0F, 1.0F, 6.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(21, 0).addBox(-1.0F, -3.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
}
