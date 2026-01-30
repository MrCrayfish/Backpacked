package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManagementInventory implements Container
{
    private final ServerPlayer player;
    private final int size;

    public ManagementInventory(ServerPlayer player)
    {
        this.player = player;
        this.size = getMaxEquipable();
    }

    @Override
    public int getContainerSize()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        return BackpackHelper.getFirstBackpackStack(this.player).isEmpty();
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
        if(!stack.isEmpty())
        {
            BackpackHelper.setBackpackStack(this.player, ItemStack.EMPTY, index);
        }
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        BackpackHelper.setBackpackStack(this.player, stack, index);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        UnlockableSlots slots = BackpackHelper.getBackpackUnlockableSlots(this.player);
        return slots.isUnlocked(index) && stack.getItem() instanceof BackpackItem;
    }

    @Override
    public boolean canTakeItem(Container container, int index, ItemStack stack)
    {
        UnlockableSlots slots = BackpackHelper.getBackpackUnlockableSlots(this.player);
        return slots.isUnlocked(index);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player)
    {
        return this.player.isAlive() && this.size == getMaxEquipable();
    }

    @Override
    public void clearContent()
    {
        for(int i = 0; i < this.size; i++)
        {
            BackpackHelper.setBackpackStack(this.player, ItemStack.EMPTY, i);
        }
    }

    public static int getMaxEquipable()
    {
        return Config.BACKPACK.equipable.maxEquipable.get();
    }
}
