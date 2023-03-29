package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import com.mrcrayfish.framework.Registration;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

/**
 * Author: MrCrayfish
 */
public class FabricPlatformHelper implements IPlatformHelper
{
    public static final CreativeModeTab TAB = FabricItemGroupBuilder.create(new ResourceLocation(Constants.MOD_ID, "creative_tab")).icon(() -> new ItemStack(ModItems.BACKPACK.get())).appendItems(itemStacks -> {
        Registration.get(Registry.ITEM_REGISTRY).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
            itemStacks.add(new ItemStack((Item) entry.get()));
        });
        Registration.get(Registry.ENCHANTMENT_REGISTRY).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
            Enchantment enchantment = (Enchantment) entry.get();
            itemStacks.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
        });
    }).build();

    @Override
    public CreativeModeTab getCreativeModTab()
    {
        return TAB;
    }
}
