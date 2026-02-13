package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class ForgeLivingEntityMixin
{
    @Unique
    private InteractionHand backpacked$hand;

    // Capturing like this to satisfy both Forge and Bukkit Hybrid servers
    @ModifyArg(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), index = 0)
    private InteractionHand backpacked$CheckBackpackForTotem_Normal(InteractionHand hand)
    {
        this.backpacked$hand = hand;
        return hand;
    }

    @ModifyVariable(method = "checkTotemDeathProtection", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), index = 7)
    private ItemStack backpacked$CheckBackpackForTotem(ItemStack original)
    {
        if(!original.is(Items.TOTEM_OF_UNDYING) && this.backpacked$hand == InteractionHand.OFF_HAND)
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
