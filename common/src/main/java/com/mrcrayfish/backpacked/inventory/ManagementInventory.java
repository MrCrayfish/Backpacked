package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.BackpackHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManagementInventory implements Container
{
    public static final int SIZE = 1;

    private final ServerPlayer player;

    public ManagementInventory(ServerPlayer player)
    {
        this.player = player;
    }

    @Override
    public int getContainerSize()
    {
        return SIZE;
    }

    @Override
    public boolean isEmpty()
    {
        return BackpackHelper.getBackpackStack(this.player).isEmpty();
    }

    @Override
    public ItemStack getItem(int index)
    {
        return BackpackHelper.getBackpackStack(this.player, index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        return count > 0 ? BackpackHelper.getBackpackStack(this.player, index).split(count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        ItemStack stack = BackpackHelper.getBackpackStack(this.player, index);
        if(stack.isEmpty())
            return ItemStack.EMPTY;
        BackpackHelper.setBackpackStack(this.player, ItemStack.EMPTY, index);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        BackpackHelper.setBackpackStack(this.player, stack, index);
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
        for(int i = 0; i < SIZE; i++)
        {
            BackpackHelper.setBackpackStack(this.player, ItemStack.EMPTY, i);
        }
    }
}
