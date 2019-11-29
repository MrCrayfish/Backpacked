package com.mrcrayfish.backpacked.inventory;

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
    public BackpackInventory()
    {
        super(9);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            ItemStack backpack = ((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0);
            return !backpack.isEmpty();
        }
        return false;
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        this.clear();
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            ItemStack backpack = ((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0);
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
    }

    @Override
    public void closeInventory(PlayerEntity player)
    {
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            ItemStack backpack = ((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0);
            if(!backpack.isEmpty())
            {
                ItemStack copy = backpack.copy();
                CompoundNBT compound = copy.getTag();
                if(compound == null)
                {
                    compound = new CompoundNBT();
                }
                ListNBT list = new ListNBT();
                InventoryHelper.saveAllItems(list, this);
                compound.put("Items", list);
                copy.setTag(compound);
                player.inventory.setInventorySlotContents(41, copy);
            }
        }
    }
}
