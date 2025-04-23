package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Author: MrCrayfish
 */
public interface IPlatformHelper
{
    void addEnchantedBookToCreativeTab(CreativeModeTab.Output output, Holder.Reference<Enchantment> enchantment);

    boolean isModLoaded(String modId);

    boolean isBuiltinOrModResourcePack(PackLocationInfo info);
}
