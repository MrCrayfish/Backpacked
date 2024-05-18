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
public class StandardBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "standard");

    public StandardBackpack()
    {
        super(null, null);
    }

    @Override
    public boolean isUnlocked(Player player)
    {
        return true;
    }
}
