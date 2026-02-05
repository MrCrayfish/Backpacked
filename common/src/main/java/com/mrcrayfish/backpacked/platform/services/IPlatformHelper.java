package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public interface IPlatformHelper
{
    boolean isModLoaded(String modId);

    boolean testPredicate(Optional<BlockPredicate> optional, BlockState state, @Nullable CompoundTag tag);

    Predicate<ItemStack> getValidProjectiles(ItemStack weapon);

    boolean isRepairable(ItemStack stack);

    CreativeModeTab.Output createCreativeTabOutput(Consumer<ItemStack> consumer);

    boolean isBuiltinOrModResourcePack(String info, PackSource source);
}
