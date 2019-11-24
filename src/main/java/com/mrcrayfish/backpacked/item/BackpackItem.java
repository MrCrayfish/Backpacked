package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.entity.player.ExtendedPlayerInventory;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public BackpackItem(Properties properties)
    {
        super(properties);
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "backpack"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if(playerIn.inventory instanceof ExtendedPlayerInventory)
        {
            ExtendedPlayerInventory inventory = (ExtendedPlayerInventory) playerIn.inventory;
            if(inventory.getBackpackItems().get(0).isEmpty())
            {
                playerIn.inventory.setInventorySlotContents(41, heldItem.copy());
                heldItem.setCount(0);
                playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
                return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
            }
        }
        return new ActionResult<>(ActionResultType.FAIL, heldItem);
    }
}
