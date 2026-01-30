package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.services.IBackpackHelper;
import com.mrcrayfish.framework.api.FrameworkAPI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class FabricBackpackHelper implements IBackpackHelper
{
    @Override
    public Item createBackpackItem(Item.Properties properties)
    {
        return new BackpackItem(properties);
    }

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
    public void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int ownerId, int backpackIndex, int cols, int rows, boolean owner, UnlockableSlots slots, Pagination pagination, Augments augments, Component title, UnlockableSlots bays)
    {
        FrameworkAPI.openMenuWithData(openingPlayer, new SimpleMenuProvider((id, playerInventory, entity) -> {
            return new BackpackContainerMenu(id, openingPlayer.getInventory(), inventory, ownerId, backpackIndex, cols, rows, owner, slots, pagination, augments, bays);
        }, title), buf -> new BackpackContainerData(backpackIndex, cols, rows, owner, slots, pagination, augments, bays).encode(buf));
    }

    @Override
    public NonNullList<ItemStack> getSimpleContainerItems(SimpleContainer container)
    {
        return container.items;
    }
}
