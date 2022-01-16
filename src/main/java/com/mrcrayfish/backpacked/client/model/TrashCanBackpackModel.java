package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class TrashCanBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/trash_can_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 8.5, -7);

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer strap;

    public TrashCanBackpackModel()
    {
        this.texWidth = 64;
        this.texHeight = 64;
        this.backpack = new ModelRenderer(this);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(21, 15).addBox(-1.5F, -2.0F, 2.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        this.bag.texOffs(0, 15).addBox(-3.5F, -1.0F, -0.5F, 7.0F, 1.0F, 7.0F, 0.0F, false);
        this.bag.texOffs(0, 0).addBox(-3.0F, -0.5F, 0.0F, 6.0F, 9.0F, 6.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(0.0F, 9.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(0, 23).addBox(2.0F, -9.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(18, 0).addBox(3.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(0, 23).addBox(-3.0F, -9.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(18, 0).addBox(-4.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
