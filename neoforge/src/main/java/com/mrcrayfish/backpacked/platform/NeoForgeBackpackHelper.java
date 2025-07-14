package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class NeoForgeBackpackHelper implements IBackpackHelper
{
    @Override
    public Optional<ItemStack> getStackInBackpackSlot(Player player)
    {
        return Optional.of(this.getBackpackStack(player));
    }

    @Override
    public ItemStack getBackpackStack(Player player)
    {
        return ModSyncedDataKeys.BACKPACK.getValue(player);
    }

    @Override
    public boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(!(stack.getItem() instanceof BackpackItem) && !stack.isEmpty())
            return false;
        ModSyncedDataKeys.BACKPACK.setValue(player, stack);
        return true;
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
    public void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int cols, int rows, boolean owner, UnlockedSlots slots, Component title)
    {
        FrameworkAPI.openMenuWithData(openingPlayer, new SimpleMenuProvider((id, playerInventory, entity) -> {
            return new BackpackContainerMenu(id, openingPlayer.getInventory(), inventory, cols, rows, owner, slots);
        }, title), new BackpackContainerData(cols, rows, owner, slots));
    }

    @Override
    public BackpackItem createBackpackItem(Item.Properties properties)
    {
        int rows = Config.SERVER.backpack.inventorySizeRows.get();
        int cols = Config.SERVER.backpack.inventorySizeColumns.get();
        return new BackpackItem(rows, cols, properties);
    }
}
