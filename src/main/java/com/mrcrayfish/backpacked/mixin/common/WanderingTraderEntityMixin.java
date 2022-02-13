package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
