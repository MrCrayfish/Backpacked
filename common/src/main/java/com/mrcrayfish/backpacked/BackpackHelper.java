package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackpackHelper
{
    public static ItemStack getBackpackStack(Player player)
    {
        return ModSyncedDataKeys.BACKPACK.getValue(player);
    }

    public static boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(stack.is(ModItems.BACKPACK.get()))
        {
            ModSyncedDataKeys.BACKPACK.setValue(player, stack);
            return true;
        }
        return false;
    }
}
