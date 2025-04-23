package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.fml.ModList;

/**
 * Author: MrCrayfish
 */
public class NeoForgePlatformHelper implements IPlatformHelper
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

    @Override
    public boolean isBuiltinOrModResourcePack(PackLocationInfo info)
    {
        if(info.source() == PackSource.BUILT_IN) return true;
        if(info.id().equals("mod_resources")) return true;
        if(info.id().equals("mod_data")) return true;
        if(info.knownPackInfo().stream().anyMatch(pack -> pack.namespace().equals("minecraft"))) return true;
        return false;
    }
}
