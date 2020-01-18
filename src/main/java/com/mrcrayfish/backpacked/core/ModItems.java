package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.integration.Baubles;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.item.BaubleBackpackItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final List<Item> ITEMS = new ArrayList<>();

    public static Item backpack;

    public static void init()
    {
        backpack = registerBackpack();
    }

    private static Item registerBackpack()
    {
        Item item;
        if(Backpacked.isBaublesLoaded())
        {
            item = Baubles.getBaubleBackpackItem();
        }
        else
        {
            item = new BackpackItem();
        }
        ITEMS.add(item);
        return item;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void register(final RegistryEvent.Register<Item> event)
    {
        ITEMS.forEach(item -> event.getRegistry().register(item));
    }
}
