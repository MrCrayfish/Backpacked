package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class MiniChestBackpack extends Backpack
{
    public MiniChestBackpack()
    {
        super(new ResourceLocation(Reference.MOD_ID, "mini_chest"));
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.MINI_CHEST;
    }
}
