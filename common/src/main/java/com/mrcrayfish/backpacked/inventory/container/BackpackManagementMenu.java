package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.container.slot.ConditionalSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackManagementMenu extends AbstractContainerMenu
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");

    private final Inventory inventory;
    private final Container container;

    public BackpackManagementMenu(int windowId, Inventory inventory)
    {
        this(windowId, inventory, new SimpleContainer(1));
    }

    public BackpackManagementMenu(int windowId, Inventory inventory, Container container)
    {
        super(ModContainers.MANAGEMENT.get(), windowId);
        this.inventory = inventory;
        this.container = container;
        this.addSlot(new ConditionalSlot(container, 0, 80, 8, stack -> {
            return stack.getItem() instanceof BackpackItem;
        }).setIcon(EMPTY_SLOT));
        this.addInventorySlots(inventory);
    }

    private void addInventorySlots(Inventory inventory)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 38 + y * 18));
            }
        }
        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 96));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int clickedSlotIndex)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(clickedSlotIndex);
        if(clickedSlot.hasItem())
        {
            ItemStack stack = clickedSlot.getItem();
            copy = stack.copy();
            if(clickedSlotIndex < 1)
            {
                if(!this.moveItemStackTo(stack, 1, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(stack, 0, 1, false))
            {
                return ItemStack.EMPTY;
            }

            if(stack.isEmpty())
            {
                clickedSlot.setByPlayer(ItemStack.EMPTY);
            }
            else
            {
                clickedSlot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.container.stillValid(player);
    }

    public Player getPlayer()
    {
        return this.inventory.player;
    }
}
