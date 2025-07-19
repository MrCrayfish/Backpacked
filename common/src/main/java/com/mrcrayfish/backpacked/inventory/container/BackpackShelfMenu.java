package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
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

public class BackpackShelfMenu extends CustomContainerMenu
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");
    private final Container managementContainer;
    private final Container shelfContainer;

    public BackpackShelfMenu(int windowId, Inventory playerInventory)
    {
        this(windowId, playerInventory, new SimpleContainer(ManagementInventory.SIZE), new SimpleContainer(ShelfBlockEntity.SIZE));
    }

    public BackpackShelfMenu(int windowId, Inventory playerInventory, Container managementContainer, Container shelfContainer)
    {
        super(ModContainers.BACKPACK_SHELF.get(), windowId);
        this.managementContainer = managementContainer;
        this.shelfContainer = shelfContainer;

        this.addSlot(new Slot(shelfContainer, 0, (176 - 18) / 2 + 1, 1));

        int managementSize = managementContainer.getContainerSize();
        for(int i = 0; i < managementSize; i++)
        {
            this.addSlot(new ConditionalSlot(managementContainer, i, (176 - (managementSize * 18)) / 2 + 1, 60, stack -> {
                return stack.getItem() instanceof BackpackItem;
            }).setIcon(EMPTY_SLOT));
        }
        this.addPlayerInventorySlots(playerInventory, 8, 102);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            copy = slotStack.copy();
            if (index < ShelfBlockEntity.SIZE)
            {
                if(!this.moveItemStackTo(slotStack, ShelfBlockEntity.SIZE, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, 0, ShelfBlockEntity.SIZE, false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.managementContainer.stillValid(player) && this.shelfContainer.stillValid(player);
    }
}
