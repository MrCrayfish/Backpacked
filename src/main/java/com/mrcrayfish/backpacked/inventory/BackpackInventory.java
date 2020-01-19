package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class BackpackInventory extends InventoryBasic
{
    public BackpackInventory(int rows)
    {
        super("container.backpacked.backpack", false, 9 * rows);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return !Backpacked.getBackpackStack(player).isEmpty();
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        this.clear();
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(!backpack.isEmpty())
        {
            NBTTagCompound compound = backpack.getTagCompound();
            if(compound != null)
            {
                if(compound.hasKey("Items", Constants.NBT.TAG_LIST))
                {
                    InventoryHelper.loadAllItems(compound.getTagList("Items", Constants.NBT.TAG_COMPOUND), this);
                }
            }
        }
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(!backpack.isEmpty())
        {
            NBTTagCompound compound = backpack.getTagCompound();
            if(compound == null)
            {
                compound = new NBTTagCompound();
            }
            NBTTagList list = new NBTTagList();
            InventoryHelper.saveAllItems(list, this);
            compound.setTag("Items", list);
            backpack.setTagCompound(compound);
        }
    }
}
