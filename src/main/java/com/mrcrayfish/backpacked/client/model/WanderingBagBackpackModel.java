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
public class WanderingBagBackpackModel extends BackpackModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/wandering_bag_backpack.png");

    public WanderingBagBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(10, 19).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 8.0F, 4.0F, new CubeDeformation(0.2F)).texOffs(0, 19).addBox(-1.0F, 1.375F, 3.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(22, 0).addBox(5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 27).mirror().addBox(6.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(22, 0).mirror().addBox(0.0F, -8.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 27).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }
}
