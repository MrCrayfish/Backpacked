package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin
{
    @Inject(method = "fireImmune", at = @At(value = "HEAD"), cancellable = true)
    public void backpacked$FireImmuneHead(CallbackInfoReturnable<Boolean> cir)
    {
        ItemEntity entity = (ItemEntity) (Object) this;
        ItemStack stack = entity.getItem();
        if(stack.getItem() == ModItems.BACKPACK.get())
        {
            HolderLookup<Enchantment> lookup = entity.level().holderLookup(Registries.ENCHANTMENT);
            if(EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(ModEnchantments.IMBUED_HIDE), stack) > 0)
            {
                cir.setReturnValue(true);
            }
        }
    }
}
