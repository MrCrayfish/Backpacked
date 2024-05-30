package com.mrcrayfish.backpacked.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("unused")
@Mixin(EnchantmentCategory.class)
public abstract class EnchantmentCategoryMixin
{
    @Shadow
    public abstract boolean canEnchant(Item item);
}
