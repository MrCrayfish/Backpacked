package com.mrcrayfish.backpacked.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @ModifyExpressionValue(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack backpacked$CheckBackpackForTotem(ItemStack original, @Local(ordinal = 0) InteractionHand hand)
    {
        if(!original.is(Items.TOTEM_OF_UNDYING) && hand == InteractionHand.OFF_HAND)
        {
            LivingEntity entity = (LivingEntity) (Object) this;
            if(entity instanceof Player player)
            {
                ItemStack stack = AugmentHandler.locateTotemOfUndying(player);
                if(!stack.isEmpty())
                {
                    ModSyncedDataKeys.IMMORTAL_COOLDOWN.setValue(player, Config.AUGMENTS.immortal.cooldown.get());
                    return stack;
                }
            }
        }
        return original;
    }
}
