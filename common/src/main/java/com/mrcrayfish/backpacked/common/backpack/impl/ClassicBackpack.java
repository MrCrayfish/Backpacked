package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ClassicBackpack extends Backpack
{
    public ClassicBackpack()
    {
        super(new ResourceLocation(Constants.MOD_ID, "classic"));
    }

    @Override
    public boolean isUnlocked(Player player)
    {
        return true;
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ModelInstances.get()::getClassic;
    }
}
