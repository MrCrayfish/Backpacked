package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import net.minecraft.world.entity.player.Player;

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
