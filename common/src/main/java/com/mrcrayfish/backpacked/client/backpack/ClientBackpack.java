package com.mrcrayfish.backpacked.client.backpack;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ClientBackpack extends Backpack // TODO DONE
{
    private final ResourceLocation baseModel;
    private final ResourceLocation strapsModel;

    public ClientBackpack(Backpack backpack)
    {
        super(backpack.getUnlockChallenge());
        this.setup(backpack.getId());
        ResourceLocation id = backpack.getId();
        String name = "backpacked/" + id.getPath();
        this.baseModel = new ResourceLocation(id.getNamespace(), name);
        this.strapsModel = new ResourceLocation(id.getNamespace(), name + "_straps");
    }

    public ResourceLocation getBaseModel()
    {
        return this.baseModel;
    }

    public ResourceLocation getStrapsModel()
    {
        return this.strapsModel;
    }
}
