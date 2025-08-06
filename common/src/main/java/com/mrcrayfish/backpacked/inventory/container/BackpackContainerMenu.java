package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackContainerMenu extends CustomContainerMenu
{
    // There is a technical hard limit of 256, these values allow the widest and tallest inventory possible
    public static final int MAX_COLUMNS = 23;
    public static final int MAX_ROWS = 11;

    private final Container backpackInventory;
    private final int cols;
    private final int rows;
    private final boolean owner;
    private final int backpackIndex;
    private final int totalBackpacks;
    private final UnlockableController controller;

    public BackpackContainerMenu(int id, Inventory playerInventory, BackpackContainerData data)
    {
        this(id, playerInventory, new SimpleContainer(Mth.clamp(data.columns(), 1, MAX_COLUMNS) * Mth.clamp(data.rows(), 1, MAX_ROWS)), data.columns(), data.rows(), data.owner(), data.slots(), data.index(), data.total());
    }

    public BackpackContainerMenu(int id, Inventory playerInventory, Container backpackContainer, int cols, int rows, boolean owner, UnlockableSlots slots, int backpackIndex, int totalBackpacks)
    {
        super(ModContainers.BACKPACK.get(), id);
        this.backpackInventory = backpackContainer;
        this.cols = Mth.clamp(cols, 1, MAX_COLUMNS);
        this.rows = Mth.clamp(rows, 1, MAX_ROWS);
        this.owner = owner;
        this.backpackIndex = backpackIndex;
        this.totalBackpacks = totalBackpacks;
        this.controller = new BackpackUnlockableController(this, slots);

        checkContainerSize(backpackContainer, this.cols * this.rows);
        backpackContainer.startOpen(playerInventory.player);

        int backpackWidth = 11 + Math.max(9 * 18, this.cols * 18) + 11;
        int backpackSlotWidth = this.cols * 18;
        int backpackSlotsX = Math.max((backpackWidth - backpackSlotWidth) / 2, 0) + 1;
        int backpackSlotsY = 28;
        for(int y = 0; y < rows; y++)
        {
            for(int x = 0; x < cols; x++)
            {
                this.addSlot(new BackpackSlot(this.controller, backpackContainer, x + y * cols, backpackSlotsX + x * 18, backpackSlotsY + y * 18));
            }
        }

        int inventorySlotsWidth = 9 * 18;
        int inventorySlotsX = Math.max((backpackWidth - inventorySlotsWidth) / 2, 0) + 1;
        int inventorySlotsY = 26 + this.rows * 18 + 15 + 3 + 19;
        this.addPlayerInventorySlots(playerInventory, inventorySlotsX, inventorySlotsY);
    }

    public Container getBackpackInventory()
    {
        return this.backpackInventory;
    }

    public int getCols()
    {
        return this.cols;
    }

    public int getRows()
    {
        return this.rows;
    }

    public boolean isOwner()
    {
        return this.owner;
    }

    public int getBackpackIndex()
    {
        return this.backpackIndex;
    }

    public int getTotalBackpacks()
    {
        return this.totalBackpacks;
    }

    public UnlockableController getController()
    {
        return this.controller;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return this.backpackInventory.stillValid(playerIn);
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
            if (index < this.rows * this.cols)
            {
                if(!this.moveItemStackTo(slotStack, this.rows * this.cols, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, 0, this.rows * this.cols, false))
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
    public void removed(Player playerIn)
    {
        super.removed(playerIn);
        this.backpackInventory.stopOpen(playerIn);
    }

    private ItemStack getBackpackStack()
    {
        if(this.backpackInventory instanceof ShelfBlockEntity.BackpackShelfContainer container)
        {
            return container.getBlockEntity().getBackpack();
        }
        if(this.backpackInventory instanceof BackpackInventory inventory)
        {
            return inventory.getBackpackStack();
        }
        return ItemStack.EMPTY;
    }

    public void openManagement(ServerPlayer player)
    {
        if(this.backpackInventory instanceof ShelfBlockEntity.BackpackShelfContainer container)
        {
            container.getBlockEntity().openShelfManagement(player);
        }
        else if(this.backpackInventory instanceof BackpackInventory)
        {
            BackpackItem.openBackpackManagement(player);
        }
    }

    private static class BackpackUnlockableController extends UnlockableController
    {
        private final BackpackContainerMenu menu;

        private BackpackUnlockableController(BackpackContainerMenu menu, UnlockableSlots slots)
        {
            super(slots);
            this.menu = menu;
        }

        @Override
        public CostModel costModel()
        {
            return Config.BACKPACK.inventory.slots.unlockCost;
        }

        @Override
        public void handleUnlockSlot(ServerPlayer player, int slotIndex, int containerIndex)
        {
            if(!this.menu.stillValid(player))
                return;

            ItemStack backpack = this.menu.getBackpackStack();
            if(backpack.isEmpty())
                return;

            UnlockableSlots slots = backpack.get(ModDataComponents.UNLOCKABLE_SLOTS.get());
            if(slots == null || !slots.isUnlockable(containerIndex))
                return;

            // Ensure the player has the experience levels
            int experienceLevelCost = this.getNextUnlockCost();
            if(!player.isCreative() && player.experienceLevel < experienceLevelCost)
                return;

            // Take the experience levels from the player
            player.giveExperienceLevels(-experienceLevelCost);

            // Finally unlock the slot and sync the changes to the client
            slots = slots.unlockSlot(containerIndex);
            backpack.set(ModDataComponents.UNLOCKABLE_SLOTS.get(), slots);
            this.slots = slots;

            // Ensure shelf saves the changes
            this.menu.getBackpackInventory().setChanged();

            // Sync to players that are currently in the same menu
            UnlockableSlots finalSlots = slots;
            List<ServerPlayer> players = player.server.getPlayerList().getPlayers();
            players.forEach(otherPlayer -> {
                if(!(otherPlayer.containerMenu instanceof BackpackContainerMenu otherMenu))
                    return;
                if(this.menu.getBackpackInventory() != otherMenu.getBackpackInventory())
                    return;
                if(otherPlayer != player) {
                    otherMenu.controller.slots = finalSlots;
                }
                Network.PLAY.sendToPlayer(() -> otherPlayer, new MessageSyncUnlockSlot(slotIndex));
            });
        }
    }
}
