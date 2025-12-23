package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.PaymentItem;
import com.mrcrayfish.backpacked.common.PaymentType;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
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

    public int getMaxSlots()
    {
        return this.cachedSlots.getMaxSlots();
    }

    public final boolean unlockSlot(int slot)
    {
        UnlockableSlots before = this.cachedSlots;
        this.cachedSlots = this.cachedSlots.unlockSlot(slot);
        return !Objects.equals(this.cachedSlots, before);
    }

    public final boolean isSlotUnlocked(int slot)
    {
        return this.cachedSlots.isUnlocked(slot);
    }

    public final int getNextUnlockCost(int numberOfSlots)
    {
        return this.cachedSlots.nextUnlockCost(this.getCostModel(), numberOfSlots);
    }

    public final boolean canAffordNextSlot(Player player, int numberOfSlots)
    {
        if(player.isCreative())
            return true;

        CostModel model = this.getCostModel();
        if(model.getPaymentType() == PaymentType.EXPERIENCE)
        {
            return player.experienceLevel >= this.getNextUnlockCost(numberOfSlots);
        }
        else if(model.getPaymentType() == PaymentType.ITEM)
        {
            PaymentItem payment = this.getPaymentItem();
            int nextCost = this.getNextUnlockCost(numberOfSlots);
            List<Container> containers = this.getPaymentContainers();
            return InventoryHelper.hasRemovableItemAndCount(payment.getItem(), nextCost, containers);
        }
        return false;
    }

    private Optional<Runnable> getPaymentJob(Player player, int numberOfSlots)
    {
        if(player.isCreative())
            return Optional.of(() -> {}); // Simply do nothing

        CostModel model = this.getCostModel();
        if(model.getPaymentType() == PaymentType.EXPERIENCE)
        {
            int cost = this.getNextUnlockCost(numberOfSlots);
            if(player.experienceLevel >= cost)
            {
                return Optional.of(() -> player.giveExperienceLevels(-cost));
            }
        }
        else if(model.getPaymentType() == PaymentType.ITEM)
        {
            PaymentItem payment = this.getPaymentItem();
            int nextCost = this.getNextUnlockCost(numberOfSlots);
            List<Container> containers = this.getPaymentContainers();
            return InventoryHelper.createRemoveItemJob(payment.getItem(), nextCost, containers);
        }
        return Optional.empty();
    }

    public boolean handleUnlockSlot(ServerPlayer player, int containerIndex)
    {
        Optional<UnlockableSlots> slotsOptional = this.getSlots(player);
        if(slotsOptional.isEmpty())
            return false;

        UnlockableSlots slots = slotsOptional.get();
        if(!slots.isUnlockable(containerIndex))
            return false;

        Optional<Runnable> paymentJob = this.getPaymentJob(player, 1);
        if(paymentJob.isEmpty())
            return false;

        paymentJob.get().run(); // Consumes experience/items
        slots = slots.unlockSlot(containerIndex);
        this.setSlots(player, slots);
        this.cachedSlots = slots;
        return true;
    }
}
