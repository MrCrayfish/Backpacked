package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ClassicBackpack extends Backpack
{
    public ClassicBackpack()
    {
        super(new ResourceLocation(Reference.MOD_ID, "classic"));
    }

    @Override
    public boolean isUnlocked(PlayerEntity player)
    {
        return true;
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.CLASSIC;
    }
}
