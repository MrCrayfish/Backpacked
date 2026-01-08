package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTraderRenderer.class)
public class WanderingTraderRendererMixin
{
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTrader;Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;F)V", at = @At(value = "TAIL"))
    private void backpacked$ApplyBackpackState(WanderingTrader trader, VillagerRenderState state, float partialTick, CallbackInfo ci)
    {
        TraderPickpocketing.get(trader).ifPresent(data -> {
            state.setData(ClientHandler.WEARING_BACKPACK, data.isBackpackEquipped());
        });
    }
}
