package com.mrcrayfish.backpacked.client.backpack;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ClientBackpack extends Backpack
{
    private final ModelResourceLocation baseModel;
    private final ModelResourceLocation strapsModel;

    public ClientBackpack(Backpack backpack)
    {
        super(backpack.getUnlockChallenge());
        this.setup(backpack.getId());
        ResourceLocation id = backpack.getId();
        String name = "backpacked/" + id.getPath();
        this.baseModel = FrameworkClientAPI.createModelResourceLocation(id.getNamespace(), name);
        this.strapsModel = FrameworkClientAPI.createModelResourceLocation(id.getNamespace(), name + "_straps");
    }

    public ModelResourceLocation getBaseModel()
    {
        this.checkSetup();
        return this.baseModel;
    }

    public ModelResourceLocation getStrapsModel()
    {
        this.checkSetup();
        return this.strapsModel;
    }
}
