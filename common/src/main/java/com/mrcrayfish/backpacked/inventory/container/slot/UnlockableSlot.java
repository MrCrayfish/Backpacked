package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.inventory.container.UnlockableController;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class UnlockableSlot extends Slot
{
    private final int slot;
    private final UnlockableController controller;
    private @Nullable Predicate<ItemStack> predicate;
    private @Nullable Pair<ResourceLocation, ResourceLocation> icon;

    public UnlockableSlot(UnlockableController controller, Container container, int index, int x, int y)
    {
        super(container, index, x, y);
        this.slot = index;
        this.controller = controller;
    }

    public UnlockableSlot setPredicate(@Nullable Predicate<ItemStack> predicate)
    {
        this.predicate = predicate;
        return this;
    }

    public UnlockableSlot setIcon(@Nullable ResourceLocation icon)
    {
        this.icon = Pair.of(InventoryMenu.BLOCK_ATLAS, icon);
        return this;
    }

    public boolean isUnlocked()
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean isActive()
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        if(this.predicate != null && !this.predicate.test(stack))
            return false;
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean isHighlightable()
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
    {
        return this.isActive() ? this.icon : null;
    }
}
