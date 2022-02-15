package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(WanderingTrader.class)
public class WanderingTraderEntityMixin
{
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void constructorTail(CallbackInfo ci)
    {
        WanderingTraderEvents.onConstructWanderingTrader((WanderingTrader) (Object) this);
    }

    @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    public void mobInteractHead(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if(!player.level.isClientSide())
        {
            WanderingTrader trader = (WanderingTrader) (Object) this;
            if(!Config.COMMON.dislikedPlayersCanTrade.get() && PickpocketChallenge.get(trader).map(data -> data.isBackpackEquipped() && data.isDislikedPlayer(player)).orElse(false))
            {
                trader.setUnhappyCounter(20);
                trader.level.playSound(null, trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
