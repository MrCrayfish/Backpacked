package com.mrcrayfish.backpacked.inventory.container.slot;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ConditionalSlot extends Slot
{
    private final Predicate<ItemStack> predicate;
    private @Nullable Identifier icon;

    public ConditionalSlot(Container container, int index, int x, int y, Predicate<ItemStack> predicate)
    {
        super(container, index, x, y);
        this.predicate = predicate;
    }

    public ConditionalSlot setIcon(@Nullable Identifier icon)
    {
        this.icon = icon;
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return this.predicate.test(stack);
    }

    @Override
    public @Nullable Identifier getNoItemIcon()
    {
        return this.icon;
    }
}
