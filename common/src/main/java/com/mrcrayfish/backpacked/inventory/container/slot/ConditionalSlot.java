package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ConditionalSlot extends Slot
{
    private final Predicate<ItemStack> predicate;
    private @Nullable ResourceLocation icon;

    public ConditionalSlot(Container container, int index, int x, int y, Predicate<ItemStack> predicate)
    {
        super(container, index, x, y);
        this.predicate = predicate;
    }

    public ConditionalSlot setIcon(@Nullable ResourceLocation icon)
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
    public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
    {
        return this.icon != null ? Pair.of(InventoryMenu.BLOCK_ATLAS, this.icon) : null;
    }
}
