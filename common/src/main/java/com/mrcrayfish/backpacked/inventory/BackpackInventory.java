package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Author: MrCrayfish
 */
public class BackpackInventory extends SimpleContainer
{
    private final int index;
    private final int columns;
    private final int rows;
    private final Player player;
    private final ItemStack stack;
    private final boolean slotsUnlocked;
    private boolean save;

    public BackpackInventory(int index, int columns, int rows, Player player, ItemStack stack)
    {
        super(rows * columns);
        this.index = index;
        this.columns = columns;
        this.rows = rows;
        this.player = player;
        this.stack = stack;
        this.slotsUnlocked = Config.SERVER.backpack.inventory.slots.unlockAll.get();
        this.loadBackpackContents(player);
    }

    private void loadBackpackContents(Player player)
    {
        ItemContainerContents contents = this.stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.getItems()); // TODO reimplement dropping items if inventory is resized
        this.spawnItemsFromLockedSlots();
    }

    private void spawnItemsFromLockedSlots()
    {
        if(!(this.stack.getItem() instanceof BackpackItem item))
            return;

        UnlockedSlots slots = item.getUnlockedSlots(this.stack);
        if(slots == null)
            return;

        for(int i = 0; i < this.getContainerSize(); i++)
        {
            ItemStack stack = this.getItem(i);
            if(!stack.isEmpty() && !slots.isUnlocked(i))
            {
                InventoryHelper.spawnStack(stack, player.level(), player.position());
                this.setChanged();
            }
        }
    }

    public ItemStack getBackpackStack()
    {
        return this.stack;
    }

    @Override
    public boolean stillValid(Player player)
    {
        if(!this.player.isAlive())
            return false;
        if(this.columns != Config.SERVER.backpack.inventory.size.columns.get())
            return false;
        if(this.rows != Config.SERVER.backpack.inventory.size.rows.get())
            return false;
        if(this.slotsUnlocked != Config.SERVER.backpack.inventory.slots.unlockAll.get())
            return false;
        if(!BackpackHelper.getBackpackStack(this.player, this.index).equals(this.stack))
            return false;
        return this.player.equals(player) || PickpocketUtil.canPickpocketEntity(this.player, player, Config.SERVER.pickpocketing.maxReachDistance.get() + 0.5);
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        this.save = true;
    }

    public void tick()
    {
        if(this.save)
        {
            this.saveItemsToStack();
            this.save = false;
        }
    }

    public void saveItemsToStack()
    {
        this.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }
}
