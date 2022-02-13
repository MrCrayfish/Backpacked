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

/**
 * Author: MrCrayfish
 */
public class TrashCanBackpackModel extends BackpackModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/trash_can_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 8.5, -7);

    public TrashCanBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(21, 15).addBox(-1.5F, -2.0F, 2.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 15).addBox(-3.5F, -1.0F, -0.5F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-3.0F, -0.5F, 0.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.ZERO);
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(0, 23).addBox(2.0F, -9.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(18, 0).mirror().addBox(3.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 23).mirror().addBox(-3.0F, -9.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(18, 0).addBox(-4.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
