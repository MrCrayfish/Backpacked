package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.ModelSupplier;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

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
    public Supplier<Object> getModelSupplier()
    {
        return () -> ModelInstances.CLASSIC;
    }
}
