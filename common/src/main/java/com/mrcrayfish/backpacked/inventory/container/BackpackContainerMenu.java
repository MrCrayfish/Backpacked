package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.PaymentItem;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
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
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class BackpackContainerMenu extends CustomContainerMenu implements SyncUnlockableSlots
{
    // There is a technical hard limit of 256, these values allow the widest and tallest inventory possible
    public static final int MAX_COLUMNS = 23;
    public static final int MAX_ROWS = 11;

    private final Container backpackInventory;
    private final int ownerId;
    private final int backpackIndex;
    private final int cols;
    private final int rows;
    private final boolean owner;
    private final UnlockableController slotController;
    private final UnlockableController augmentBayController;
    private final Pagination pagination;
    private Augments augments;

    public BackpackContainerMenu(int id, Inventory playerInventory, BackpackContainerData data)
    {
        this(id, playerInventory, new SimpleContainer(Mth.clamp(data.columns(), 1, MAX_COLUMNS) * Mth.clamp(data.rows(), 1, MAX_ROWS)), -1, data.backpackIndex(), data.columns(), data.rows(), data.owner(), data.slots(), data.pagination(), data.augments(), data.bays());
    }

    public BackpackContainerMenu(int id, Inventory playerInventory, Container backpackContainer, int ownerId, int backpackIndex, int cols, int rows, boolean owner, UnlockableSlots slots, Pagination pagination, Augments augments, UnlockableSlots bays)
    {
        super(ModContainers.BACKPACK.get(), id);
        this.backpackInventory = backpackContainer;
        this.ownerId = ownerId;
        this.backpackIndex = backpackIndex;
        this.cols = Mth.clamp(cols, 1, MAX_COLUMNS);
        this.rows = Mth.clamp(rows, 1, MAX_ROWS);
        this.owner = owner;
        this.pagination = pagination;
        this.slotController = new BackpackUnlockableController(this, slots, List.of(playerInventory, backpackContainer));
        this.augmentBayController = new AugmentUnlockableController(this, bays, List.of(playerInventory, backpackContainer));
        this.augments = augments;

        checkContainerSize(backpackContainer, this.cols * this.rows);
        backpackContainer.startOpen(playerInventory.player);

        int backpackWidth = 11 + Math.max(9 * 18, this.cols * 18) + 11;
        int backpackSlotsWidth = this.cols * 18;
        int backpackSlotsX = Math.max((backpackWidth - backpackSlotsWidth) / 2, 0) + 1;
        int backpackSlotsY = 28;
        for(int y = 0; y < rows; y++)
        {
            for(int x = 0; x < cols; x++)
            {
                this.addSlot(new BackpackSlot(this.slotController, backpackContainer, x + y * cols, backpackSlotsX + x * 18, backpackSlotsY + y * 18));
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

    public int getOwnerId()
    {
        return ownerId;
    }

    public int getBackpackIndex()
    {
        return this.backpackIndex;
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

    public Pagination getPagination()
    {
        return this.pagination;
    }

    public UnlockableController getSlotController()
    {
        return this.slotController;
    }

    public Augments getAugments()
    {
        return this.augments;
    }

    public void setAugments(Augments augments)
    {
        this.augments = augments;
    }

    public UnlockableController getAugmentBayController()
    {
        return this.augmentBayController;
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
            BackpackItem.openBackpackManagement(player, true);
        }
    }

    @Override
    public void handleSyncSlots(ServerPlayer unlockingPlayer, List<UnlockableSlot> unlockedSlots)
    {
        List<Integer> slotIndexes = unlockedSlots.stream().map(slot -> slot.index).toList();
        List<ServerPlayer> players = unlockingPlayer.server.getPlayerList().getPlayers();
        players.forEach(otherPlayer -> {
            if(otherPlayer.containerMenu instanceof BackpackContainerMenu otherMenu) {
                if(this.backpackInventory == otherMenu.backpackInventory) {
                    if(otherPlayer != unlockingPlayer) {
                        otherMenu.slotController.cachedSlots = this.slotController.cachedSlots;
                    }
                    Network.PLAY.sendToPlayer(() -> otherPlayer, new MessageSyncUnlockSlot(slotIndexes));
                }
            }
        });
    }

    private static class BackpackUnlockableController extends UnlockableController
    {
        private final BackpackContainerMenu menu;
        private final List<Container> paymentContainers;

        private BackpackUnlockableController(BackpackContainerMenu menu, UnlockableSlots slots, List<Container> paymentContainers)
        {
            super(slots);
            this.menu = menu;
            this.paymentContainers = paymentContainers;
        }

        @Override
        public Optional<UnlockableSlots> getSlots(Player player)
        {
            ItemStack backpack = this.menu.getBackpackStack();
            if(!backpack.isEmpty())
            {
                return Optional.ofNullable(backpack.get(ModDataComponents.UNLOCKABLE_SLOTS.get()));
            }
            return Optional.empty();
        }

        @Override
        public void setSlots(Player player, UnlockableSlots slots)
        {
            ItemStack backpack = this.menu.getBackpackStack();
            if(!backpack.isEmpty())
            {
                backpack.set(ModDataComponents.UNLOCKABLE_SLOTS.get(), slots);
            }
        }

        @Override
        public CostModel getCostModel()
        {
            return Config.BACKPACK.inventory.slots.unlockCost;
        }

        @Override
        public PaymentItem getPaymentItem()
        {
            return Config.getInventoryPaymentItem();
        }

        @Override
        public List<Container> getPaymentContainers()
        {
            return this.paymentContainers;
        }

        @Override
        public boolean allowsUnlockToken()
        {
            return Config.BACKPACK.inventory.slots.allowUnlockingUsingUnlockToken.get();
        }
    }

    private static class AugmentUnlockableController extends UnlockableController
    {
        private final BackpackContainerMenu menu;
        private final List<Container> paymentContainers;

        private AugmentUnlockableController(BackpackContainerMenu menu, UnlockableSlots bays, List<Container> paymentContainers)
        {
            super(bays);
            this.menu = menu;
            this.paymentContainers = paymentContainers;
        }

        @Override
        public Optional<UnlockableSlots> getSlots(Player player)
        {
            ItemStack backpack = this.menu.getBackpackStack();
            if(!backpack.isEmpty())
            {
                return Optional.ofNullable(BackpackHelper.getUnlockableAugmentBays(backpack));
            }
            return Optional.empty();
        }

        @Override
        public void setSlots(Player player, UnlockableSlots slots)
        {
            ItemStack backpack = this.menu.getBackpackStack();
            if(!backpack.isEmpty())
            {
                backpack.set(ModDataComponents.UNLOCKABLE_AUGMENT_BAYS.get(), slots);
            }
        }

        @Override
        public CostModel getCostModel()
        {
            return Config.BACKPACK.augmentBays.unlockCost;
        }

        @Override
        public PaymentItem getPaymentItem()
        {
            return Config.getAugmentBayPaymentItem();
        }

        @Override
        public List<Container> getPaymentContainers()
        {
            return this.paymentContainers;
        }

        @Override
        public boolean allowsUnlockToken()
        {
            return Config.BACKPACK.augmentBays.allowUnlockingUsingUnlockToken.get();
        }
    }
}
