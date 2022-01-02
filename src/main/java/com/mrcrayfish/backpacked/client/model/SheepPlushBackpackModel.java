package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class SheepPlushBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/sheep_plush_backpack.png");

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer head;
    private final ModelRenderer strap;

    public SheepPlushBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this);
        this.backpack.setPos(0.0F, 24.0F, 0.0F);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(0, 0).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 9.0F, 4.0F, 0.0F, false);
        this.bag.texOffs(20, 5).addBox(1.0F, 7.0F, 4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.bag.texOffs(20, 5).addBox(-3.0F, 7.0F, 4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.bag.texOffs(20, 5).addBox(-3.0F, 1.0F, 4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.bag.texOffs(20, 5).addBox(1.0F, 1.0F, 4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.head = new ModelRenderer(this);
        this.bag.addChild(this.head);
        setRotationAngle(this.head, 0.6981F, 0.0F, 0.0F);
        this.head.texOffs(0, 21).addBox(-1.5F, 0.5F, 4.75F, 3.0F, 1.0F, 0.0F, 0.0F, false);
        this.head.texOffs(16, 0).addBox(-1.5F, -0.5F, 3.5F, 3.0F, 3.0F, 1.0F, 0.0F, false);
        this.head.texOffs(0, 13).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.bag.addChild(this.strap);
        this.strap.texOffs(16, 9).addBox(2.0F, 0.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(20, 0).addBox(3.0F, 7.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(16, 9).addBox(-3.0F, 0.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(20, 0).addBox(-4.0F, 7.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
