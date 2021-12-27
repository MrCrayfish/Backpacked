package com.mrcrayfish.backpacked.inventory.container;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.inventory.container.slot.InventoryBackpackSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ExtendedPlayerContainer extends PlayerContainer
{
    public ExtendedPlayerContainer(PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn)
    {
        super(playerInventory, localWorld, playerIn);
        this.addSlot(new InventoryBackpackSlot(playerInventory, 41, 77, 44));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            copy = slotStack.copy();
            EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(copy);
            if(index != 46 && copy.getItem() instanceof BackpackItem)
            {
                if(!this.slots.get(46).hasItem())
                {
                    if(!this.moveItemStackTo(slotStack, 46, 47, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if(index == 0)
            {
                if(!this.moveItemStackTo(slotStack, 9, 45, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(slotStack, copy);
            }
            else if(index < 5)
            {
                if(!this.moveItemStackTo(slotStack, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 9)
            {
                if(!this.moveItemStackTo(slotStack, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR && !this.slots.get(8 - equipmentslottype.getIndex()).hasItem())
            {
                int i = 8 - equipmentslottype.getIndex();
                if(!this.moveItemStackTo(slotStack, i, i + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(equipmentslottype == EquipmentSlotType.OFFHAND && !this.slots.get(45).hasItem())
            {
                if(!this.moveItemStackTo(slotStack, 45, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 46)
            {
                if(!this.moveItemStackTo(slotStack, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 36)
            {
                if(!this.moveItemStackTo(slotStack, 36, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 45)
            {
                if(!this.moveItemStackTo(slotStack, 9, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, 9, 45, false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if(slotStack.getCount() == copy.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, slotStack);
            if(index == 0)
            {
                playerIn.drop(itemstack2, false);
            }
        }

        return copy;
    }
}
