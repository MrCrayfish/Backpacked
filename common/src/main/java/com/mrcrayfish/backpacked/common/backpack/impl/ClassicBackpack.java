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
        super(null, null);
    }

    @Override
    public boolean isUnlocked(Player player)
    {
        return true;
    }
}
