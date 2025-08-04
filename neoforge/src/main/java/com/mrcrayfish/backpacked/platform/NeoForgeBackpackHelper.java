package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.services.IBackpackHelper;
import com.mrcrayfish.framework.api.FrameworkAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class NeoForgeBackpackHelper implements IBackpackHelper
{
    @Override
    public boolean isBackpackVisible(Player player)
    {
        return true;
    }

    @Override
    public ShelfBlockEntity createShelfBlockEntityType(BlockPos pos, BlockState state)
    {
        return new ShelfBlockEntity(pos, state);
    }

    @Override
    public void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int cols, int rows, boolean owner, UnlockableSlots slots, int index, int total, Component title)
    {
        FrameworkAPI.openMenuWithData(openingPlayer, new SimpleMenuProvider((id, playerInventory, entity) -> {
            return new BackpackContainerMenu(id, openingPlayer.getInventory(), inventory, cols, rows, owner, slots, index, total);
        }, title), new BackpackContainerData(cols, rows, owner, slots, index, total));
    }

    @Override
    public BackpackItem createBackpackItem(Item.Properties properties)
    {
        return new BackpackItem(properties);
    }
}
