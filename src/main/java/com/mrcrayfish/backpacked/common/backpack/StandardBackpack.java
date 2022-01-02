package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class StandardBackpack extends Backpack
{
    public StandardBackpack()
    {
        super(new ResourceLocation(Reference.MOD_ID, "standard"));
    }

    @Override
    public boolean isUnlocked(PlayerEntity player)
    {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BackpackModel getModel()
    {
        return ModelInstances.STANDARD;
    }
}
