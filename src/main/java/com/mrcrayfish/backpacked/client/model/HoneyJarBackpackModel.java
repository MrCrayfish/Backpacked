package com.mrcrayfish.backpacked.client.model;

import com.mojang.math.Vector3d;
import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class HoneyJarBackpackModel extends BackpackModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/honey_jar_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 8, -7);

    public HoneyJarBackpackModel(ModelPart root)
    {
        super(root, TEXTURE, RenderType::entityTranslucent);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 14).addBox(-2.5F, -1.0F, 0.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 20).addBox(-2.5F, 0.5F, 0.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(22, 10).addBox(5.0F, -8.0F, -4.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(18, 0).mirror().addBox(6.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(22, 10).mirror().addBox(0.0F, -8.0F, -4.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(18, 0).addBox(-1.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void tickForPlayer(Vec3 pos, Player player)
    {
        if(player.hurtTime != 0 && player.hurtTime == player.hurtDuration - 1)
        {
            for(int i = 0; i < 5; i++)
            {
                player.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HONEY_BLOCK.defaultBlockState()), pos.x, pos.y + 0.25, pos.z, 0, 0, 0);
            }
        }
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
