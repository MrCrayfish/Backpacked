package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class FabricPlayerMixin
{
    @Inject(method = "getProjectile", at = @At(value = "RETURN"), cancellable = true)
    public void backpacked$LocateAmmo(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir)
    {
        Player player = (Player) (Object) this;
        ItemStack ammo = AugmentHandler.locateAmmunition(player, weapon, cir.getReturnValue());
        if(!ammo.isEmpty())
        {
            cir.setReturnValue(ammo);
        }
    }

    @Inject(method = "dropEquipment", at = @At(value = "TAIL"))
    private void backpacked$DropBackpack(CallbackInfo ci)
    {
        Player player = (Player) (Object) this;
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        if(serverPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
            return;

        if(Config.BACKPACK.equipable.keepOnDeath.get())
            return;

        NonNullList<ItemStack> removed = BackpackHelper.removeAllBackpacks(player);
        for(int index = 0; index < removed.size(); index++)
        {
            ItemStack stack = removed.get(index);
            if(!stack.isEmpty())
            {
                RecallAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.RECALL.get());
                if(augment != null && AugmentHandler.recallBackpack(serverPlayer, index, stack, augment)) {
                    return;
                }
                player.drop(stack, true, false);
            }
        }
    }
}
