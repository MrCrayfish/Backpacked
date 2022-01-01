package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class HoneyJarBackpack extends Backpack
{
    public HoneyJarBackpack()
    {
        super(new ResourceLocation(Reference.MOD_ID, "honey_jar"));
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.HONEY_JAR;
    }
}
