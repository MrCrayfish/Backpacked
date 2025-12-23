package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import com.mrcrayfish.backpacked.common.backpack.BackpackState;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.inventory.container.UnlockableContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.Hopper;

import java.util.List;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class BackpackInventory extends UnlockableContainer
{
    private final int index;
    private final Player player;
    private final ItemStack stack;
    private final BackpackState state;
    private boolean save;

    public BackpackInventory(int index, int columns, int rows, Player player, ItemStack stack)
    {
        super(rows * columns);
        this.index = index;
        this.player = player;
        this.stack = stack;
        this.state = BackpackState.create(stack);
        this.loadBackpackContents();
    }

    private void loadBackpackContents()
    {
        ItemContainerContents contents = this.stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.items);
        this.spawnItemsFromLockedSlots();

        NonNullList<ItemStack> allItems = NonNullList.withSize(256, ItemStack.EMPTY);
        contents.copyInto(allItems);
        this.spawnTrailingItems(allItems);
    }

    private void spawnTrailingItems(List<ItemStack> allItems)
    {
        for(int i = this.items.size(); i < allItems.size(); i++)
        {
            ItemStack stack = allItems.get(i);
            if(!stack.isEmpty())
            {
                InventoryHelper.spawnStack(stack, this.player.level(), this.player.position());
            }
        }
    }

    private void spawnItemsFromLockedSlots()
    {
        if(!(this.stack.getItem() instanceof BackpackItem item))
            return;

        UnlockableSlots slots = item.getUnlockableSlots(this.stack);
        if(slots == null)
            return;

        for(int i = 0; i < this.items.size(); i++)
        {
            ItemStack stack = this.items.get(i);
            if(!stack.isEmpty() && !slots.isUnlocked(i))
            {
                InventoryHelper.spawnStack(stack, this.player.level(), this.player.position());
                this.setChanged();
            }
        }
    }

    public ItemStack getBackpackStack()
    {
        return this.stack;
    }

    public BackpackState getState()
    {
        return this.state;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return super.canPlaceItem(slot, stack) && isAllowedItem(stack);
    }

    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack stack)
    {
        if(container instanceof Hopper)
        {
            HopperBridgeAugment augment = BackpackHelper.findAugment(this.stack, ModAugmentTypes.HOPPER_BRIDGE.get());
            if(augment != null)
            {
                if(!augment.extract())
                    return false;

                if(augment.filterMode().checkExtract() && !augment.isFilteringItem(stack.getItem()))
                    return false;
            }
        }
        return super.canTakeItem(container, slot, stack);
    }

    @Override
    protected UnlockableSlots getUnlockableSlots()
    {
        if(this.stack.getItem() instanceof BackpackItem item)
        {
            return item.getUnlockableSlots(this.stack);
        }
        return UnlockableSlots.ALL;
    }

    @Override
    public boolean stillValid(Player player)
    {
        if(this.stack.isEmpty())
            return false;
        if(!this.player.isAlive())
            return false;
        if(this.getState().isInvalid())
            return false;
        if(!BackpackHelper.getBackpackStack(this.player, this.index).equals(this.stack))
            return false;
        return this.player.equals(player) || PickpocketUtil.canPickpocketEntity(this.player, player, Config.PICKPOCKETING.maxReachDistance.get() + 0.5);
    }

    @Override
    public void setChanged()
    {
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
        if(!this.stack.isEmpty())
        {
            this.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
        }
    }

    public static boolean isAllowedItem(ItemStack stack)
    {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(Config.getBannedItemsList().contains(id))
            return false;

        if(stack.is(Items.BUNDLE))
            return false;

        return stack.getItem().canFitInsideContainerItems();
    }

    public ItemStack findFirst(Predicate<ItemStack> predicate)
    {
        for(int i = 0; i < this.getContainerSize(); i++)
        {
            ItemStack stack = this.getItem(i);
            if(!stack.isEmpty() && predicate.test(stack))
            {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
