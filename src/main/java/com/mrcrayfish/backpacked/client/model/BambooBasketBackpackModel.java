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
public class BambooBasketBackpackModel extends BackpackModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/bamboo_basket_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 9, -7);

    public BambooBasketBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-4.0F, -0.25F, -0.5F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.ZERO);
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(24, 12).addBox(5.0F, -8.0F, -4.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 25).addBox(6.0F, -3.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(24, 12).addBox(0.0F, -8.0F, -4.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(21, 0).addBox(-1.0F, -3.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
