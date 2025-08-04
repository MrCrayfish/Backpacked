package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.inventory.container.UnlockableController;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackSlot extends UnlockableSlot
{
    public BackpackSlot(UnlockableController controller, Container container, int index, int x, int y)
    {
        super(controller, container, index, x, y);
        this.setPredicate(BackpackSlot::isAllowedItem);
    }

    public static boolean isAllowedItem(ItemStack stack)
    {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(Config.getBannedItemsList().contains(id))
            return false;

        if(stack.is(Items.BUNDLE))
            return false;

        return stack.getItem().canFitInsideContainerItems();
    }
}
