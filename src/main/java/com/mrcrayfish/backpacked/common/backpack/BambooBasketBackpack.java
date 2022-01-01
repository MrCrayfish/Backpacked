package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class BambooBasketBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "bamboo_basket");

    public BambooBasketBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.BAMBOO_BASKET;
    }
}
