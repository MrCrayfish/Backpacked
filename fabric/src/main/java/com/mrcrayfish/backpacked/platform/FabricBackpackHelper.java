package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.integration.Trinkets;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.services.IBackpackHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class FabricBackpackHelper implements IBackpackHelper
{
    @Override
    public ItemStack getStackInBackpackSlot(Player player)
    {
        return Trinkets.getStackInBackpackSlot(player);
    }

    @Override
    public ItemStack getBackpackStack(Player player)
    {
        return Trinkets.getBackpackStack(player);
    }

    @Override
    public boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(!(stack.getItem() instanceof BackpackItem) && !stack.isEmpty())
            return false;
        Trinkets.setBackpackStack(player, stack);
        return true;
    }

    @Override
    public EnchantmentCategory getEnchantmentCategory()
    {
        return Backpacked.ENCHANTMENT_TYPE.get();
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
    public void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int cols, int rows, boolean owner, Component title)
    {
        openingPlayer.openMenu(new BackpackScreenFactory(inventory, cols, rows, owner, title));
    }

    @Override
    public BackpackItem createBackpackItem(Item.Properties properties)
    {
        return Trinkets.createTrinketBackpack(properties);
    }

    private record BackpackScreenFactory(Container inventory, int cols, int rows, boolean owner, Component title) implements ExtendedScreenHandlerFactory
    {
        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf)
        {
            buf.writeVarInt(this.cols);
            buf.writeVarInt(this.rows);
            buf.writeBoolean(this.owner);
        }

        @Override
        public Component getDisplayName()
        {
            return this.title;
        }

        @Override
        public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
        {
            return new BackpackContainerMenu(windowId, player.getInventory(), this.inventory, this.cols, this.rows, this.owner);
        }
    }
}
