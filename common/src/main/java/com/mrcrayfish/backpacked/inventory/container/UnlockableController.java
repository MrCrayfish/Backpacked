package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.PaymentItem;
import com.mrcrayfish.backpacked.common.PaymentType;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockSlot;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public abstract class UnlockableController
{
    protected UnlockableSlots cachedSlots;

    public UnlockableController(UnlockableSlots slots)
    {
        this.cachedSlots = slots;
    }

    public abstract Optional<UnlockableSlots> getSlots(Player player);

    public abstract void setSlots(Player player, UnlockableSlots slots);

    public abstract CostModel getCostModel();

    public abstract PaymentItem getPaymentItem();

    public abstract List<Container> getPaymentContainers();

    public final void unlockSlot(int slot)
    {
        this.cachedSlots = this.cachedSlots.unlockSlot(slot);
    }

    public final boolean isSlotUnlocked(int slot)
    {
        return this.cachedSlots.isUnlocked(slot);
    }

    public final boolean isSlotUnlockable(int slot)
    {
        return this.cachedSlots.isUnlockable(slot);
    }

    public final int getNextUnlockCost()
    {
        return this.cachedSlots.nextUnlockCost(this.getCostModel());
    }

    public final boolean canAffordNextSlot(Player player)
    {
        if(player.isCreative())
            return true;

        CostModel model = this.getCostModel();
        if(model.getPaymentType() == PaymentType.EXPERIENCE)
        {
            return player.experienceLevel >= this.getNextUnlockCost();
        }
        else if(model.getPaymentType() == PaymentType.ITEM)
        {
            PaymentItem payment = this.getPaymentItem();
            int nextCost = this.getNextUnlockCost();
            List<Container> containers = this.getPaymentContainers();
            return InventoryHelper.hasRemovableItemAndCount(payment.getItem(), nextCost, containers);
        }
        return false;
    }

    private Optional<Runnable> getPaymentJob(Player player)
    {
        if(player.isCreative())
            return Optional.of(() -> {}); // Simply do nothing

        CostModel model = this.getCostModel();
        if(model.getPaymentType() == PaymentType.EXPERIENCE)
        {
            int cost = this.getNextUnlockCost();
            if(player.experienceLevel >= cost)
            {
                return Optional.of(() -> player.giveExperienceLevels(-cost));
            }
        }
        else if(model.getPaymentType() == PaymentType.ITEM)
        {
            PaymentItem payment = this.getPaymentItem();
            int nextCost = this.getNextUnlockCost();
            List<Container> containers = this.getPaymentContainers();
            return InventoryHelper.createRemoveItemJob(payment.getItem(), nextCost, containers);
        }
        return Optional.empty();
    }

    public void handleUnlockSlot(ServerPlayer player, int slotIndex, int containerIndex)
    {
        Optional<UnlockableSlots> slotsOptional = this.getSlots(player);
        if(slotsOptional.isEmpty())
            return;

        UnlockableSlots slots = slotsOptional.get();
        if(!slots.isUnlockable(containerIndex))
            return;

        Optional<Runnable> paymentJob = this.getPaymentJob(player);
        if(paymentJob.isEmpty())
            return;

        paymentJob.get().run(); // Consumes experience/items
        slots = slots.unlockSlot(containerIndex);
        this.setSlots(player, slots);
        this.cachedSlots = slots;

        this.onSlotUnlocked(player, slotIndex);
    }

    protected void onSlotUnlocked(ServerPlayer player, int slotIndex)
    {
        // Sync the unlock change to the player
        Network.PLAY.sendToPlayer(() -> player, new MessageSyncUnlockSlot(slotIndex));
    }
}
