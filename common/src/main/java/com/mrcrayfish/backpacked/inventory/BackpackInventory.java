package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.platform.Services;
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
    private final Player player;
    private final ItemStack stack;
    private boolean save;

    public BackpackInventory(int columns, int rows, Player player, ItemStack stack)
    {
        super(rows * columns);
        this.player = player;
        this.stack = stack;
        this.loadBackpackContents(player);
    }

    private void loadBackpackContents(Player player)
    {
        ItemContainerContents contents = this.stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.getItems()); // TODO reimplement dropping items if inventory is resized
        /*if(compound.contains("Items", Tag.TAG_LIST))
        {
            InventoryHelper.loadAllItems(compound.getList("Items", Tag.TAG_COMPOUND), this, player.level(), player.position());
        }*/
    }

    public ItemStack getBackpackStack()
    {
        return this.stack;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.player.isAlive() && (Services.BACKPACK.getBackpackStack(this.player).equals(this.stack) && (this.player.equals(player) || PickpocketUtil.canPickpocketEntity(this.player, player, Config.SERVER.pickpocketing.maxReachDistance.get() + 0.5)));
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
