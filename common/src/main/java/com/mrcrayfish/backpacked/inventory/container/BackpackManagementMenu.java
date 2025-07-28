package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackManagementMenu extends CustomContainerMenu implements UnlockableController
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private UnlockedSlots unlockedSlots;

    public BackpackManagementMenu(int windowId, Inventory inventory, ManagementContainerData customData)
    {
        this(windowId, inventory, new SimpleContainer(ManagementInventory.getMaxEquipable()), new SimpleContainerData(1), customData.slots());
    }

    public BackpackManagementMenu(int windowId, Inventory inventory, Container container, ContainerData data, UnlockedSlots slots)
    {
        super(ModContainers.MANAGEMENT.get(), windowId);
        this.inventory = inventory;
        this.container = container;
        this.data = data;
        this.unlockedSlots = slots;
        checkContainerDataCount(data, 1);
        for(int i = 0; i < container.getContainerSize(); i++)
        {
            this.addSlot(new UnlockableSlot(this, container, i, i * 18 + (176 - (container.getContainerSize() * 18)) / 2 + 1, 27)
                    .setIcon(EMPTY_SLOT)
                    .setPredicate(stack -> stack.getItem() instanceof BackpackItem)
            );
        }
        this.addPlayerInventorySlots(inventory, 8, 79);
        this.addDataSlots(data);
    }

    public Container getContainer()
    {
        return this.container;
    }

    public boolean hasNothingEquipped()
    {
        return this.data.get(0) == 0;
    }

    public UnlockedSlots getUnlockedSlots()
    {
        return this.unlockedSlots;
    }

    @Override
    public void unlockSlot(int slot)
    {
        this.unlockedSlots = this.unlockedSlots.unlockSlot(slot);
    }

    @Override
    public boolean isSlotUnlocked(int slot)
    {
        return this.unlockedSlots.isUnlocked(slot);
    }

    @Override
    public boolean canUnlockSlot(int slot)
    {
        return this.unlockedSlots.isUnlockable(slot);
    }

    @Override
    public void handleUnlockSlot(ServerPlayer player, int slot)
    {
        UnlockedSlots slots = BackpackHelper.getBackpackUnlockedSlots(player);
        if(!slots.isUnlockable(slot))
            return;

        // Ensure the player has the experience levels
        int experienceLevelCost = slots.nextBackpackSlotUnlockCost();
        if(!player.isCreative() && player.experienceLevel < experienceLevelCost)
            return;

        // Take the experience levels from the player
        player.giveExperienceLevels(-experienceLevelCost);

        // Finally unlock the slot and sync the changes to the client
        slots = slots.unlockSlot(slot);
        BackpackHelper.setBackpackUnlockedSlots(player, slots);
        this.unlockedSlots = slots;

        // Sync to players that are currently in the same menu
        Network.PLAY.sendToPlayer(() -> player, new MessageSyncUnlockSlot(slot));
    }

    @Override
    public int getNextUnlockCost()
    {
        return this.unlockedSlots.nextBackpackSlotUnlockCost();
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
            if(clickedSlotIndex < this.container.getContainerSize())
            {
                if(!this.moveItemStackTo(stack, this.container.getContainerSize(), this.slots.size(), false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(stack, 0, this.container.getContainerSize(), false))
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
