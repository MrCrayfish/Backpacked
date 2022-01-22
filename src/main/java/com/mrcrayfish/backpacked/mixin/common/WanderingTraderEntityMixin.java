package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(WanderingTraderEntity.class)
public class WanderingTraderEntityMixin
{
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void constructorTail(CallbackInfo ci)
    {
        WanderingTraderEvents.onConstructWanderingTrader((WanderingTraderEntity) (Object) this);
    }

    @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    public void mobInteractHead(PlayerEntity entity, Hand hand, CallbackInfoReturnable<ActionResultType> cir)
    {
        if(!entity.level.isClientSide())
        {
            WanderingTraderEntity trader = (WanderingTraderEntity) (Object) this;
            if(!Config.COMMON.dislikedPlayersCanTrade.get() && PickpocketChallenge.get(trader).map(data -> data.isBackpackEquipped() && data.isDislikedPlayer(entity)).orElse(false))
            {
                trader.setUnhappyCounter(20);
                trader.level.playSound(null, trader, SoundEvents.VILLAGER_NO, SoundCategory.NEUTRAL, 1.0F, 1.5F);
                cir.setReturnValue(ActionResultType.SUCCESS);
            }
        }
    }
}
