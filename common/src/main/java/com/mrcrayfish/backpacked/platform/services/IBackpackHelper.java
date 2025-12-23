package com.mrcrayfish.backpacked.platform.services;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public interface IBackpackHelper
{
    boolean isBackpackVisible(Player player);

    ShelfBlockEntity createShelfBlockEntityType(BlockPos pos, BlockState state);

    void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int ownerId, int backpackIndex, int cols, int rows, boolean owner, UnlockableSlots slots, Pagination pagination, Augments augments, Component title, UnlockableSlots bays);

    BackpackItem createBackpackItem(Item.Properties properties);
}
