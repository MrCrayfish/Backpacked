package com.mrcrayfish.backpacked.inventory;

import com.google.common.base.Preconditions;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManagementInventory implements Container
{
    private final ServerPlayer player;

    public ManagementInventory(ServerPlayer player)
    {
        this.player = player;
    }

    @Override
    public int getContainerSize()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return ModSyncedDataKeys.BACKPACK.getValue(this.player).isEmpty();
    }

    @Override
    public ItemStack getItem(int index)
    {
        Preconditions.checkArgument(index == 0);
        return ModSyncedDataKeys.BACKPACK.getValue(this.player);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        Preconditions.checkArgument(index == 0);
        return count > 0 ? ModSyncedDataKeys.BACKPACK.getValue(this.player).split(count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        Preconditions.checkArgument(index == 0);
        ItemStack stack = ModSyncedDataKeys.BACKPACK.getValue(this.player);
        if(stack.isEmpty())
            return ItemStack.EMPTY;
        ModSyncedDataKeys.BACKPACK.setValue(this.player, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        ModSyncedDataKeys.BACKPACK.setValue(this.player, stack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player)
    {
        return this.player.isAlive();
    }

    @Override
    public void clearContent()
    {
        ModSyncedDataKeys.BACKPACK.setValue(this.player, ItemStack.EMPTY);
    }
}
