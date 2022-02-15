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
public class PiglinPackBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/piglin_pack_backpack.png");

    public PiglinPackBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 19).addBox(-2.5F, 5.0F, 4.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 13).addBox(-3.5F, 2.0F, 0.0F, 7.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)).texOffs(18, 0).addBox(-2.0F, 1.5F, 3.75F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)).texOffs(22, 4).addBox(-1.5F, -1.0F, 1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(18, 15).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(18, 9).mirror().addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(18, 15).mirror().addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(18, 9).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }
}
