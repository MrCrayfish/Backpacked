package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class HoneyJarBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/honey_jar_backpack.png");

    private final ModelRenderer backpack;
    private final ModelRenderer bag;
    private final ModelRenderer strap;

    public HoneyJarBackpackModel()
    {
        super(RenderType::entityTranslucent);
        this.texWidth = 32;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this);
        this.backpack.setPos(0.0F, 24.0F, 0.0F);
        this.bag = new ModelRenderer(this);
        this.backpack.addChild(this.bag);
        this.bag.texOffs(0, 0).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);
        this.bag.texOffs(0, 14).addBox(-2.5F, -1.0F, 0.5F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        this.bag.texOffs(0, 20).addBox(-2.5F, 0.5F, 0.5F, 5.0F, 7.0F, 5.0F, 0.0F, false);
        this.strap = new ModelRenderer(this);
        this.strap.setPos(-3.0F, 8.0F, 0.0F);
        this.bag.addChild(this.strap);
        this.strap.texOffs(22, 10).addBox(5.0F, -8.0F, -4.0F, 1.0F, 7.0F, 4.0F, 0.0F, false);
        this.strap.texOffs(18, 0).addBox(6.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(22, 10).addBox(0.0F, -8.0F, -4.0F, 1.0F, 7.0F, 4.0F, 0.0F, true);
        this.strap.texOffs(18, 0).addBox(-1.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
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
        if(player.hurtTime != 0 && player.hurtTime == player.hurtDuration - 1)
        {
            for(int i = 0; i < 5; i++)
            {
                player.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.HONEY_BLOCK.defaultBlockState()), pos.x, pos.y + 0.25, pos.z, 0, 0, 0);
            }
        }
    }
}
