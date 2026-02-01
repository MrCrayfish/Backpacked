package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.entity.LootCapture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(LivingEntity.class)
public class FabricLivingEntityMixin
{
    @Unique
    private InteractionHand backpacked$hand;

    @Inject(method = "dropAllDeathLoot", at = @At(value = "HEAD"))
    private void backpacked$OnDropLootPre(DamageSource damageSource, CallbackInfo ci)
    {
        if(damageSource.getEntity() instanceof ServerPlayer player)
        {
            ((LootCapture) this).backpacked$StartCapturingDrop(player);
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At(value = "TAIL"))
    private void backpacked$OnDropLootPost(DamageSource damageSource, CallbackInfo ci)
    {
        ((LootCapture) this).backpacked$EndCapturingDrop();
    }

    @Inject(method = "die", at = @At(value = "TAIL"))
    private void backpacked$OnDie(DamageSource source, CallbackInfo ci)
    {
        // Back up just in-case other mods somehow cancel dropAllDeathLoot
        ((LootCapture) this).backpacked$EndCapturingDrop();
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void backpacked$CheckBackpackForTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, InteractionHand[] hands, int var4, int var5, InteractionHand hand)
    {
        this.backpacked$hand = hand;
    }

    @ModifyVariable(method = "checkTotemDeathProtection", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0), index = 3)
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
