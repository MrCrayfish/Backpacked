package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.backpack.LevelDataState;
import com.mrcrayfish.backpacked.client.renderer.backpack.LivingEntityDataState;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
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
            if(data.isBackpackEquipped()) {
                ClientBackpack backpack = ClientRegistry.instance().getBackpack(VillagerBackpackLayer.WANDERING_BACKPACK);
                if(backpack == null)
                    return;
                BackpackRenderState backpackRenderState = new BackpackRenderState();
                ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
                backpackRenderState.bobbing = meta.bobbing();
                backpackRenderState.renderer = meta.renderer().orElse(null);
                backpackRenderState.baseModel = backpack.getBaseModel();
                backpackRenderState.strapsModel = backpack.getStrapsModel();
                backpackRenderState.entityData = LivingEntityDataState.create(trader, partialTick);
                backpackRenderState.levelData = LevelDataState.create(trader.level(), partialTick);
                backpackRenderState.visible = true;
                backpackRenderState.cosmeticProperties = CosmeticProperties.DEFAULT;
                backpackRenderState.horizontalDelta = trader.getDeltaMovement().horizontalDistance();
                state.setData(ClientHandler.BACKPACK_RENDER_STATE_KEY, backpackRenderState);
            }
        });
    }
}
