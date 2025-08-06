package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
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

public class BackpackManagementMenu extends CustomContainerMenu
{
    private static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_backpack_slot");

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;

    public BackpackManagementMenu(int windowId, Inventory inventory, ManagementContainerData customData)
    {
        this(windowId, inventory, new SimpleContainer(ManagementInventory.getMaxEquipable()), new SimpleContainerData(1), customData.slots());
    }

    public BackpackManagementMenu(int windowId, Inventory inventory, Container container, ContainerData data, UnlockableSlots slots)
    {
        super(ModContainers.MANAGEMENT.get(), windowId);
        this.inventory = inventory;
        this.container = container;
        this.data = data;
        checkContainerDataCount(data, 1);

        UnlockableController controller = new ManagementUnlockableController(slots);
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

    public boolean hasNothingEquipped()
    {
        return this.data.get(0) == 0;
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
        public ManagementUnlockableController(UnlockableSlots slots)
        {
            super(slots);
        }

        @Override
        public CostModel costModel()
        {
            return Config.BACKPACK.equipable.unlockCost;
        }

        @Override
        public void handleUnlockSlot(ServerPlayer player, int slotIndex, int containerIndex)
        {


            UnlockableSlots slots = BackpackHelper.getBackpackUnlockableSlots(player);
            if(!slots.isUnlockable(containerIndex))
                return;

            // Ensure the player has the experience levels
            int experienceLevelCost = this.getNextUnlockCost();
            if(!player.isCreative() && player.experienceLevel < experienceLevelCost)
                return;

            // Take the experience levels from the player
            player.giveExperienceLevels(-experienceLevelCost);

            // Finally unlock the slot and update unlocked slots player data
            slots = slots.unlockSlot(containerIndex);
            BackpackHelper.setBackpackUnlockableSlots(player, slots);
            this.slots = slots;

            // Sync the unlock change to the player
            Network.PLAY.sendToPlayer(() -> player, new MessageSyncUnlockSlot(slotIndex));
        }
    }
}
