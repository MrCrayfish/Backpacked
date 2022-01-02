package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class TurtleShellBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/turtle_shell_backpack.png");

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer strap;

    public TurtleShellBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 9.0F, 4.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(0, 13).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(10, 13).addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(0, 13).addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(10, 13).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
