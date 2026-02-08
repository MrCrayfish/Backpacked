package com.mrcrayfish.backpacked.platform.services;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public interface IBackpackHelper
{
    // Only here to restore compat with Legendary Tabs mod
    default ItemStack getBackpackStack(Player player)
    {
        return BackpackHelper.getFirstBackpackStack(player);
    }

    Item createBackpackItem(Item.Properties properties);

    boolean isBackpackVisible(Player player);

    ShelfBlockEntity createShelfBlockEntityType(BlockPos pos, BlockState state);

    void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int ownerId, int backpackIndex, int cols, int rows, boolean owner, UnlockableSlots slots, Pagination pagination, Augments augments, Component title, UnlockableSlots bays);

    NonNullList<ItemStack> getSimpleContainerItems(SimpleContainer container);
}
