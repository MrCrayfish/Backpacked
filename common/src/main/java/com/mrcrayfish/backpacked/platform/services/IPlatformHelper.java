package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public interface IPlatformHelper
{
    boolean isModLoaded(String modId);

    boolean isBuiltinOrModResourcePack(PackLocationInfo info);

    Predicate<ItemStack> getValidProjectiles(ItemStack weapon);

    boolean isRepairable(ItemStack stack);

    CreativeModeTab.Output createCreativeTabOutput(Consumer<ItemStack> consumer);
}
