package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IRegistrationHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

/**
 * Author: MrCrayfish
 */
public class NeoForgeRegistrationHelper implements IRegistrationHelper
{
    @Override
    public void addEnchantedBookToCreativeTab(CreativeModeTab.Output output, Enchantment enchantment)
    {
        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }
}
