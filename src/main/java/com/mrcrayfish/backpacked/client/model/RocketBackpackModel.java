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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class RocketBackpackModel extends BackpackModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/rocket_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 10, -6);

    public RocketBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(16, 27).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(12, 7).addBox(-1.0F, -3.0F, 1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 19).addBox(-2.0F, 10.0F, 1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(16, 7).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 21).mirror().addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(16, 7).mirror().addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 21).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void tickForPlayer(Vec3 pos, Player player)
    {
        if(player.isFallFlying())
        {
            player.level.addParticle(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
        }
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
