package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import com.mrcrayfish.backpacked.common.backpack.BackpackState;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.inventory.container.UnlockableContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        CompoundTag compound = this.stack.getOrCreateTag();
        if(compound.contains("Items", Tag.TAG_LIST))
        {
            InventoryHelper.loadItems(compound.getList("Items", Tag.TAG_COMPOUND), this);
            this.spawnItemsFromLockedSlots();
        }

        SimpleContainer container = new SimpleContainer(256);
        InventoryHelper.loadItems(compound.getList("Items", Tag.TAG_COMPOUND), container);
        this.spawnTrailingItems(container);

        // Store valid items back onto the item data
        ListTag list = new ListTag();
        InventoryHelper.saveAllItems(list, this);
        compound.put("Items", list);
        this.setChanged();
    }

    private void spawnTrailingItems(Container container)
    {
        for(int i = this.getContainerSize(); i < container.getContainerSize(); i++)
        {
            ItemStack stack = container.getItem(i);
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

        for(int i = 0; i < this.getContainerSize(); i++)
        {
            ItemStack stack = this.getItem(i);
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
        return UnlockableSlots.all();
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
        if(BackpackHelper.getBackpackStack(this.player, this.index) != this.stack)
            return false;
        return this.player.equals(player) || PickpocketUtil.canPickpocketEntity(this.player, player, Config.SERVER.pickpocketing.maxReachDistance.get() + 0.5);
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
        CompoundTag compound = this.stack.getOrCreateTag();
        compound.put("Items", InventoryHelper.saveAllItems(new ListTag(), this));
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
