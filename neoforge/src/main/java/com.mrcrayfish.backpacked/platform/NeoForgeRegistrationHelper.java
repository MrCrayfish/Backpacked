package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IRegistrationHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.fml.ModList;

/**
 * Author: MrCrayfish
 */
public class NeoForgeRegistrationHelper implements IRegistrationHelper
{
    @Override
    public void addEnchantedBookToCreativeTab(CreativeModeTab.Output output, Holder.Reference<Enchantment> enchantment)
    {
        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.value().getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }

    @Override
    public boolean isModLoaded(String modId)
    {
        return ModList.get().isLoaded(modId);
    }
}
