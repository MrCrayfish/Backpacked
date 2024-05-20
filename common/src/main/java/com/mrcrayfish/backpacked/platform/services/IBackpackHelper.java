package com.mrcrayfish.backpacked.platform.services;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public interface IBackpackHelper
{
    ItemStack getBackpackStack(Player player);

    boolean setBackpackStack(Player player, ItemStack stack);

    EnchantmentCategory getEnchantmentCategory();

    boolean isUsingThirdPartySlot();

    boolean isBackpackVisible(Player player);

    ShelfBlockEntity createShelfBlockEntityType(BlockPos pos, BlockState state);

    void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int cols, int rows, boolean owner, Component title);

    BackpackItem createBackpackItem(Item.Properties properties);
}
