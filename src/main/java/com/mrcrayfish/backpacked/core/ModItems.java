package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static final Item BACKPACK = null;

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void register(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BackpackItem(new Item.Properties().group(ItemGroup.MISC)));
    }
}
