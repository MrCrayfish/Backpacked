package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class RocketBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/rocket_backpack.png");

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer strap;

    public RocketBackpackModel()
    {
        this.texWidth = 32;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(0, 7).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);
        this.bag.texOffs(0, 0).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        this.bag.texOffs(16, 27).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        this.bag.texOffs(12, 7).addBox(-1.0F, -3.0F, 1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        this.bag.texOffs(16, 19).addBox(-2.0F, 10.0F, 1.0F, 4.0F, 4.0F, 1.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(16, 7).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(0, 21).addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(16, 7).addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(0, 21).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
    public void tickForPlayer(Vector3d pos, PlayerEntity player)
    {
        if(player.isFallFlying())
        {
            player.level.addParticle(ParticleTypes.LARGE_SMOKE, player.xo, player.yo, player.zo, 0, 0, 0);
        }
    }
}
