package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackpackHelper
{
    public static ItemStack getBackpackStack(Player player)
    {
        return getBackpackStack(player, 0);
    }

    public static ItemStack getBackpackStack(Player player, int index)
    {
        ItemStack stack = ModSyncedDataKeys.BACKPACKS.getValue(player).get(Mth.clamp(index, 0, 0));
        if(stack.isEmpty())
        {
            // TODO Temporary until full release
            stack = ModSyncedDataKeys.BACKPACK.getValue(player);
            if(!stack.isEmpty())
            {
                setBackpackStack(player, stack, index);
                ModSyncedDataKeys.BACKPACK.setValue(player, ItemStack.EMPTY);
            }
        }
        return stack;
    }

    public static boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(stack.is(ModItems.BACKPACK.get()))
        {
            // Keep in mind that this will not trigger a sync
            ModSyncedDataKeys.BACKPACKS.getValue(player).set(0, stack);
            return true;
        }
        return false;
    }

    public static boolean setBackpackStack(Player player, ItemStack stack, int index)
    {
        if(stack.is(ModItems.BACKPACK.get()))
        {
            // Keep in mind that this will not trigger a sync
            ModSyncedDataKeys.BACKPACKS.getValue(player).set(Mth.clamp(index, 0, 0), stack);
            return true;
        }
        return false;
    }
}
