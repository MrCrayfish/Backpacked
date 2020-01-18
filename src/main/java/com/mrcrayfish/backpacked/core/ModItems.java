package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final Item BACKPACK = register(new BackpackItem());

    private static Item register(Item item)
    {
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
