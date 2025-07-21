package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.slot.ConditionalSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackManagementMenu extends CustomContainerMenu
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");

    private final Inventory inventory;
    private final Container container;

    public BackpackManagementMenu(int windowId, Inventory inventory)
    {
        this(windowId, inventory, new SimpleContainer(5));
    }

    public BackpackManagementMenu(int windowId, Inventory inventory, Container container)
    {
        super(ModContainers.MANAGEMENT.get(), windowId);
        this.inventory = inventory;
        this.container = container;
        for(int i = 0; i < ManagementInventory.SIZE; i++)
        {
            this.addSlot(new ConditionalSlot(container, i, i * 18 + (176 - (ManagementInventory.SIZE * 18)) / 2 + 1, 12, stack -> {
                return stack.getItem() instanceof BackpackItem;
            }).setIcon(EMPTY_SLOT));
        }
        this.addPlayerInventorySlots(inventory, 8, 54);
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
            if(clickedSlotIndex < ManagementInventory.SIZE)
            {
                if(!this.moveItemStackTo(stack, ManagementInventory.SIZE, this.slots.size(), false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(stack, 0, ManagementInventory.SIZE, false))
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
