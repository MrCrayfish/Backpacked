package com.mrcrayfish.backpacked.client.model;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class StandardBackpackModel extends BackpackModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/standard_backpack.png");

    public StandardBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 9).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(22, 0).mirror().addBox(3.4239F, 3.6173F, 0.016F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 0).addBox(-3.5F, 4.0F, 0.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(10, 20).addBox(-3.5F, 4.568F, 4.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(22, 0).addBox(-4.4239F, 3.6173F, 0.016F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(18, 0).addBox(-1.0F, 1.5F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 30).addBox(-1.5F, -1.0F, 1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition part1 = bag.addOrReplaceChild("part1", CubeListBuilder.create().texOffs(22, 9).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.4239F, 8.6173F, 0.016F, 0.0F, 0.0F, -1.1781F));
        PartDefinition part2 = bag.addOrReplaceChild("part2", CubeListBuilder.create().texOffs(22, 14).mirror().addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.4239F, 3.6173F, 0.016F, 0.0F, 0.0F, 1.1781F));
        PartDefinition part3 = bag.addOrReplaceChild("part3", CubeListBuilder.create().texOffs(6, 17).addBox(-3.5F, -1.0F, -2.0F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.568F, 5.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition part4 = bag.addOrReplaceChild("part4", CubeListBuilder.create().texOffs(6, 17).addBox(-3.5F, 0.0F, -2.0F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.568F, 5.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition part5 = bag.addOrReplaceChild("part5", CubeListBuilder.create().texOffs(22, 9).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4239F, 8.6173F, 0.016F, 0.0F, 0.0F, 1.1781F));
        PartDefinition part6 = bag.addOrReplaceChild("part6", CubeListBuilder.create().texOffs(22, 14).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4239F, 3.6173F, 0.016F, 0.0F, 0.0F, -1.1781F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(0, 17).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(22, 21).mirror().addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 17).mirror().addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(22, 21).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }
}
