package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.PaymentItem;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class BackpackManagementMenu extends CustomContainerMenu
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private final boolean showInventoryButton;

    public BackpackManagementMenu(int windowId, Inventory inventory, ManagementContainerData customData)
    {
        this(windowId, inventory, new SimpleContainer(ManagementInventory.getMaxEquipable()), new SimpleContainerData(1), customData.slots(), customData.showInventoryButton());
    }

    public BackpackManagementMenu(int windowId, Inventory inventory, Container container, ContainerData data, UnlockableSlots slots, boolean showInventoryButton)
    {
        super(ModContainers.MANAGEMENT.get(), windowId);
        this.inventory = inventory;
        this.container = container;
        this.data = data;
        this.showInventoryButton = showInventoryButton;
        checkContainerDataCount(data, 1);

        UnlockableController controller = new ManagementUnlockableController(slots, List.of(inventory));
        for(int i = 0; i < container.getContainerSize(); i++)
        {
            this.addSlot(new UnlockableSlot(controller, container, i, i * 18 + (176 - (container.getContainerSize() * 18)) / 2 + 1, 27)
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

    public boolean hadNoBackpacksEquippedOnInitialOpen()
    {
        return this.data.get(0) == 0;
    }

    public boolean showInventoryButton()
    {
        return this.showInventoryButton;
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

    public static class ManagementUnlockableController extends UnlockableController
    {
        private final List<Container> paymentContainers;

        public ManagementUnlockableController(UnlockableSlots slots, List<Container> paymentContainers)
        {
            super(slots);
            this.paymentContainers = paymentContainers;
        }

        @Override
        public Optional<UnlockableSlots> getSlots(Player player)
        {
            return Optional.of(BackpackHelper.getBackpackUnlockableSlots(player));
        }

        @Override
        public void setSlots(Player player, UnlockableSlots slots)
        {
            BackpackHelper.setBackpackUnlockableSlots(player, slots);
        }

        @Override
        public CostModel getCostModel()
        {
            return Config.BACKPACK.equipable.unlockCost;
        }

        @Override
        public PaymentItem getPaymentItem()
        {
            return Config.getBackpackPaymentItem();
        }

        @Override
        public List<Container> getPaymentContainers()
        {
            return this.paymentContainers;
        }

        @Override
        public boolean allowsUnlockToken()
        {
            return Config.BACKPACK.equipable.allowUnlockingUsingUnlockToken.get();
        }
    }
}
