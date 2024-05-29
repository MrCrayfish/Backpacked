package com.mrcrayfish.backpacked.asm;

import com.chocohead.mm.api.ClassTinkerers;
import com.chocohead.mm.api.EnumAdder;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.mixin.EnchantmentCategoryMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
public class Loader implements Runnable
{
    @Override
    public void run()
    {
        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
        String enchantmentCategoryClass = resolver.mapClassName("intermediary", "net.minecraft.class_1886");
        EnumAdder adder = ClassTinkerers.enumBuilder(enchantmentCategoryClass);
        adder.addEnumSubclass("BACKPACKED$BACKPACK", Loader.class.getName() + "$BackpackCategory");
        adder.build();
    }

    public static class BackpackCategory extends EnchantmentCategoryMixin
    {
        @Override
        public boolean canEnchant(Item item)
        {
            return item instanceof BackpackItem;
        }
    }
}
