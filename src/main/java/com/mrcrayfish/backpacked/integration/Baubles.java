package com.mrcrayfish.backpacked.integration;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.item.BaubleBackpackItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class Baubles
{
    private Map<UUID, ItemStack> backpackMap = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDeathHigh(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        this.backpackMap.remove(player.getUniqueID());
        ItemStack stack = Backpacked.getBackpackStack(player);
        if(stack.getItem() instanceof BaubleBackpackItem)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.BODY.getValidSlots()[0];
            handler.setStackInSlot(index, ItemStack.EMPTY);
            this.backpackMap.put(player.getUniqueID(), stack);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathLow(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if(this.backpackMap.containsKey(player.getUniqueID()))
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.BODY.getValidSlots()[0];
            handler.setStackInSlot(index, this.backpackMap.get(player.getUniqueID()));
            this.backpackMap.remove(player.getUniqueID());
        }
    }

    public static ItemStack getBackpackStack(EntityPlayer player)
    {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        ItemStack stack = handler.getStackInSlot(BaubleType.BODY.getValidSlots()[0]);
        if(!stack.isEmpty() && stack.getItem() instanceof BaubleBackpackItem)
        {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void setBackpackStack(EntityPlayer player, ItemStack stack)
    {
        if(stack.getItem() instanceof BaubleBackpackItem)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.BODY.getValidSlots()[0];
            ItemStack remainder = handler.insertItem(index, stack.copy(), true);
            if(remainder.getCount() < stack.getCount())
            {
                handler.insertItem(index, stack.copy(), false);
            }
        }
    }

    public static Item getBaubleBackpackItem()
    {
        return new BaubleBackpackItem();
    }
}
