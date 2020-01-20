package com.mrcrayfish.backpacked.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class BaubleBackpackItem extends BackpackItem implements IBauble
{
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(playerIn);
        ItemStack stack = playerIn.getHeldItem(handIn);
        int index = BaubleType.BODY.getValidSlots()[0];
        ItemStack remainder = handler.insertItem(index, stack.copy(), true);
        if(remainder.getCount() < stack.getCount())
        {
            playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            handler.insertItem(index, stack.copy(), false);
            stack.setCount(0);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack)
    {
        return BaubleType.BODY;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }
}
