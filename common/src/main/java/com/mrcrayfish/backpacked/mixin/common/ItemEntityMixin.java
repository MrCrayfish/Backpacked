package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.ImbuedHideAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
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
        if(stack.is(ModItems.BACKPACK.get()))
        {
            ImbuedHideAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.IMBUED_HIDE.get());
            if(augment != null)
            {
                cir.setReturnValue(true);
            }
        }
    }
}
