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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class CogwheelBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/cogwheel_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 9, -7);
    private final ModelPart gear;


    public CogwheelBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
        this.gear = this.bag.getChild("gear");
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(14, 14).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(14, 14).addBox(-4.0F, 8.0F, 0.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.0F, 0.5F, 0.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition gear = bag.addOrReplaceChild("gear", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -1.0F, -0.25F, 2.0F, 2.0F, 10.0F, new CubeDeformation(-0.25F)).texOffs(30, 21).addBox(-2.0F, -2.0F, 6.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 26).addBox(-3.0F, -3.0F, 7.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(-0.125F)).texOffs(28, 0).addBox(-1.0F, -5.0F, 6.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 4.5F, 0.0F));
        PartDefinition cog1 = gear.addOrReplaceChild("cog1", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.0F, 0.0F, -0.7854F));
        PartDefinition cog2 = gear.addOrReplaceChild("cog2", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.0F, 0.0F, 0.7854F));
        PartDefinition cog3 = gear.addOrReplaceChild("cog3", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, -5.0F, -0.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(20, 22).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 14).mirror().addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(20, 22).mirror().addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 14).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAngles(@Nullable Player player, int animationTick, float partialTick)
    {
        float position = player != null ? player.animationPosition : 0F;
        float speed = player != null ? player.animationSpeed : 0F;
        this.gear.zRot = (float) Math.toRadians((animationTick + partialTick) * 4.0F + (position - speed * (1.0F - partialTick)) * 16.0F);
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
