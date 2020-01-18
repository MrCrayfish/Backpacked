package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ExtendedPlayerContainer extends ContainerPlayer
{
    public ExtendedPlayerContainer(InventoryPlayer inventoryPlayer, boolean localWorld, EntityPlayer playerIn)
    {
        super(inventoryPlayer, localWorld, playerIn);
        this.addSlotToContainer(new Slot(inventoryPlayer, 41, 77, 44)
        {
            @Nullable
            @Override
            public String getSlotTexture()
            {
                return Backpacked.EMPTY_BACKPACK_SLOT.toString();
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof BackpackItem;
            }
        });
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            copy = slotStack.copy();
            EntityEquipmentSlot equipmentSlot = EntityLiving.getSlotForItemStack(copy);
            if(index != 46 && copy.getItem() instanceof BackpackItem)
            {
                if(!this.inventorySlots.get(46).getHasStack())
                {
                    if(!this.mergeItemStack(slotStack, 46, 47, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if(index == 0)
            {
                if(!this.mergeItemStack(slotStack, 9, 45, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(slotStack, copy);
            }
            else if(index < 5)
            {
                if(!this.mergeItemStack(slotStack, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 9)
            {
                if(!this.mergeItemStack(slotStack, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(equipmentSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get(8 - equipmentSlot.getIndex()).getHasStack())
            {
                int i = 8 - equipmentSlot.getIndex();
                if(!this.mergeItemStack(slotStack, i, i + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(equipmentSlot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(45).getHasStack())
            {
                if(!this.mergeItemStack(slotStack, 45, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 46)
            {
                if(!this.mergeItemStack(slotStack, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 36)
            {
                if(!this.mergeItemStack(slotStack, 36, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 45)
            {
                if(!this.mergeItemStack(slotStack, 9, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(slotStack, 9, 45, false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if(slotStack.getCount() == copy.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, slotStack);
            if(index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return copy;
    }
}
