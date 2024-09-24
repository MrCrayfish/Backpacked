package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.blockentity.ForgeShelfBlockEntity;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.integration.item.ForgeBackpackItem;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.services.IBackpackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

/**
 * Author: MrCrayfish
 */
public class ForgeBackpackHelper implements IBackpackHelper
{
    @Override
    public ItemStack getStackInBackpackSlot(Player player)
    {
        return Curios.getStackInBackpackSlot(player);
    }

    @Override
    public ItemStack getBackpackStack(Player player)
    {
        return Curios.getBackpackStack(player);
    }

    @Override
    public boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(!(stack.getItem() instanceof BackpackItem) && !stack.isEmpty())
            return false;
        Curios.setBackpackStack(player, stack);
        return true;
    }

    @Override
    public EnchantmentCategory getEnchantmentCategory()
    {
        return Backpacked.ENCHANTMENT_TYPE;
    }

    @Override
    public boolean isBackpackVisible(Player player)
    {
        return Curios.isBackpackVisible(player);
    }

    @Override
    public ShelfBlockEntity createShelfBlockEntityType(BlockPos pos, BlockState state)
    {
        return new ForgeShelfBlockEntity(pos, state);
    }

    @Override
    public void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int cols, int rows, boolean owner, Component title)
    {
        NetworkHooks.openScreen(openingPlayer, new SimpleMenuProvider((id, playerInventory, entity) -> {
            return new BackpackContainerMenu(id, openingPlayer.getInventory(), inventory, cols, rows, owner);
        }, title), buffer -> {
            buffer.writeVarInt(cols);
            buffer.writeVarInt(rows);
            buffer.writeBoolean(owner);
        });
    }

    @Override
    public BackpackItem createBackpackItem(Item.Properties properties)
    {
        return new ForgeBackpackItem(properties);
    }
}
