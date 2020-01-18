package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public BackpackItem()
    {
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "backpack"));
        this.setUnlocalizedName(Reference.MOD_ID + ".backpack");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
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
                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, heldItem);
    }
}
