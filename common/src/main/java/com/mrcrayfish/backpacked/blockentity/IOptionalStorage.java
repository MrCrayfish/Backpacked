package com.mrcrayfish.backpacked.blockentity;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public interface IOptionalStorage extends Container
{
    @Nullable
    SimpleContainer getInventory();

    @Override
    default int getContainerSize()
    {
        return Optional.ofNullable(this.getInventory()).map(SimpleContainer::getContainerSize).orElse(0);
    }

    @Override
    default boolean isEmpty()
    {
        return Optional.ofNullable(this.getInventory()).map(SimpleContainer::isEmpty).orElse(true);
    }

    @Override
    default ItemStack getItem(int index)
    {
        return Optional.ofNullable(this.getInventory()).map(inv -> inv.getItem(index)).orElse(ItemStack.EMPTY);
    }

    @Override
    default ItemStack removeItem(int index, int count)
    {
        return Optional.ofNullable(this.getInventory()).map(inv -> inv.removeItem(index, count)).orElse(ItemStack.EMPTY);
    }

    @Override
    default ItemStack removeItemNoUpdate(int index)
    {
        return Optional.ofNullable(this.getInventory()).map(inv -> inv.removeItemNoUpdate(index)).orElse(ItemStack.EMPTY);
    }

    @Override
    default void setItem(int index, ItemStack stack)
    {
        Optional.ofNullable(this.getInventory()).ifPresent(inv -> inv.setItem(index, stack));
    }

    @Override
    default boolean stillValid(Player player)
    {
        return Optional.ofNullable(this.getInventory()).map(inv -> inv.stillValid(player)).orElse(false);
    }

    @Override
    default void clearContent()
    {
        Optional.ofNullable(this.getInventory()).ifPresent(SimpleContainer::clearContent);
    }

    @Override
    default boolean canPlaceItem(int index, ItemStack stack)
    {
        return this.getInventory() != null;
    }
}
