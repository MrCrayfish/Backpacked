package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import net.fabricmc.fabric.impl.resource.loader.BuiltinModResourcePackSource;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public void addEnchantedBookToCreativeTab(CreativeModeTab.Output output, Holder.Reference<Enchantment> enchantment)
    {
        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.value().getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }

    @Override
    public boolean isModLoaded(String modId)
    {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean isBuiltinOrModResourcePack(PackLocationInfo info)
    {
        if(info.source() == PackSource.BUILT_IN) return true;
        if(info.id().equals("fabric")) return true;
        if(info.source() == ModResourcePackCreator.RESOURCE_PACK_SOURCE) return true;
        if(info.source() instanceof BuiltinModResourcePackSource) return true;
        if(info.knownPackInfo().stream().anyMatch(pack -> pack.namespace().equals("minecraft"))) return true;
        return false;
    }

    @Override
    public Predicate<ItemStack> getValidProjectiles(ItemStack weapon)
    {
        if(weapon.getItem() instanceof ProjectileWeaponItem item)
        {
            return item.getAllSupportedProjectiles();
        }
        return stack -> false;
    }
}
