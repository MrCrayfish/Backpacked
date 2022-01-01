package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class RocketBackpack extends Backpack
{
    public RocketBackpack()
    {
        super(new ResourceLocation(Reference.MOD_ID, "rocket"));
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.ROCKET;
    }
}
