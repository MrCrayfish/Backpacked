package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
import com.mrcrayfish.backpacked.inventory.container.slot.ConditionalSlot;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BackpackShelfMenu extends CustomContainerMenu
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");
    private final Container managementContainer;
    private final Container shelfContainer;

    public BackpackShelfMenu(int windowId, Inventory playerInventory, ManagementContainerData customData)
    {
        this(windowId, playerInventory, new SimpleContainer(ManagementInventory.getMaxEquipable()), new SimpleContainer(ShelfBlockEntity.SIZE), customData.slots());
    }

    public BackpackShelfMenu(int windowId, Inventory playerInventory, Container managementContainer, Container shelfContainer, UnlockableSlots managementSlots)
    {
        super(ModContainers.BACKPACK_SHELF.get(), windowId);
        this.managementContainer = managementContainer;
        this.shelfContainer = shelfContainer;

        checkContainerSize(managementContainer, ManagementInventory.getMaxEquipable());
        checkContainerSize(shelfContainer, ShelfBlockEntity.SIZE);

        this.addSlot(new ConditionalSlot(shelfContainer, 0, (176 - 18) / 2 + 1, 1, stack -> {
            return stack.getItem() instanceof BackpackItem;
        }));

        UnlockableController controller = new BackpackManagementMenu.ManagementUnlockableController(managementSlots, List.of(playerInventory));
        int managementSize = managementContainer.getContainerSize();
        for(int i = 0; i < managementSize; i++)
        {
            this.addSlot(new UnlockableSlot(controller, managementContainer, i, i * 18 + (176 - (managementSize * 18)) / 2 + 1, 60)
                .setIcon(EMPTY_SLOT)
                .setPredicate(stack -> stack.getItem() instanceof BackpackItem)
            );
        }
        this.addPlayerInventorySlots(playerInventory, 8, 102);
    }

    public Container getManagementContainer()
    {
        return this.managementContainer;
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
                if(!this.moveItemStackTo(slotStack, ShelfBlockEntity.SIZE, this.slots.size(), false))
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
