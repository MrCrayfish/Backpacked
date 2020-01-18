package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class BackpackInventory extends Inventory
{
    public BackpackInventory(int rows)
    {
        super(9 * rows);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return !Backpacked.getBackpackStack(player).isEmpty();
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        this.clear();
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(!backpack.isEmpty())
        {
            CompoundNBT compound = backpack.getTag();
            if(compound != null)
            {
                if(compound.contains("Items", Constants.NBT.TAG_LIST))
                {
                    InventoryHelper.loadAllItems(compound.getList("Items", Constants.NBT.TAG_COMPOUND), this);
                }
            }
        }
    }

    @Override
    public void closeInventory(PlayerEntity player)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(!backpack.isEmpty())
        {
            CompoundNBT compound = backpack.getTag();
            if(compound == null)
            {
                compound = new CompoundNBT();
            }
            ListNBT list = new ListNBT();
            InventoryHelper.saveAllItems(list, this);
            compound.put("Items", list);
            backpack.setTag(compound);
        }
    }
}
