package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.Registration;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModCreativeTabs
{
    public static final RegistryEntry<CreativeModeTab> MAIN = RegistryEntry.creativeModeTab(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "creative_tab"), builder -> {
        builder.title(Component.translatable("itemGroup." + Constants.MOD_ID));
        builder.icon(() -> new ItemStack(ModItems.BACKPACK.get()));
        builder.displayItems((params, output) -> {
            Registration.get(Registries.BLOCK).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                output.accept((ItemLike) entry.get());
            });
            Registration.get(Registries.ITEM).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                output.accept((ItemLike) entry.get());
            });
            HolderLookup.RegistryLookup<Enchantment> lookup = params.holders().lookupOrThrow(Registries.ENCHANTMENT);
            Services.REGISTRATION.addEnchantedBookToCreativeTab(output, lookup.getOrThrow(ModEnchantments.FUNNELLING));
            Services.REGISTRATION.addEnchantedBookToCreativeTab(output, lookup.getOrThrow(ModEnchantments.REPAIRMAN));
            Services.REGISTRATION.addEnchantedBookToCreativeTab(output, lookup.getOrThrow(ModEnchantments.LOOTED));
            Services.REGISTRATION.addEnchantedBookToCreativeTab(output, lookup.getOrThrow(ModEnchantments.IMBUED_HIDE));
            Services.REGISTRATION.addEnchantedBookToCreativeTab(output, lookup.getOrThrow(ModEnchantments.MARKSMAN));
        });
    });
}
