package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Author: MrCrayfish
 */
public interface IRegistrationHelper
{
    void addEnchantedBookToCreativeTab(CreativeModeTab.Output output, Enchantment enchantment);

    boolean isModLoaded(String modId);
}
