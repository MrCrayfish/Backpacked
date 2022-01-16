package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class MiniChestBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/mini_chest_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 6, -6);

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer strap;

    public MiniChestBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(1, 0).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 5.0F, 0.0F, false);
        this.bag.texOffs(0, 0).addBox(-1.0F, 2.0F, 4.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(0.0F, 9.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(0, 12).addBox(2.0F, -9.0F, -4.0F, 1.0F, 6.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(10, 12).addBox(3.0F, -4.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(0, 12).addBox(-3.0F, -9.0F, -4.0F, 1.0F, 6.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(10, 12).addBox(-4.0F, -4.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
