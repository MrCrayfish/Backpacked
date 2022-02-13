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
public class CardboardBoxBackpackModel extends BackpackModel
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/cardboard_box_backpack.png");
    private static final Vector3d SHELF_OFFSET = new Vector3d(0, 8, -7);

    public CardboardBoxBackpackModel(ModelPart root)
    {
        super(root, TEXTURE);
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition mash = new MeshDefinition();
        PartDefinition root = mash.getRoot();
        PartDefinition backpack = root.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bag = backpack.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition strap = bag.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(0, 14).addBox(2.0F, -9.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(10, 14).mirror().addBox(3.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 14).mirror().addBox(-3.0F, -9.0F, -4.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(10, 14).addBox(-4.0F, -2.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));
        return LayerDefinition.create(mash, 32, 32);
    }

    @Override
    public Vector3d getShelfOffset()
    {
        return SHELF_OFFSET;
    }
}
