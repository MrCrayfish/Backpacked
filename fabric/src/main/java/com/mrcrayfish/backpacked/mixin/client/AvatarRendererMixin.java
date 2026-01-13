package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.backpack.LevelDataState;
import com.mrcrayfish.backpacked.client.renderer.backpack.LivingEntityDataState;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin
{
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At(value = "TAIL"))
    private void backpacked$ApplyBackpackState(Avatar avatar, AvatarRenderState state, float f, CallbackInfo ci)
    {
        if(avatar instanceof AbstractClientPlayer player)
        {
            CosmeticProperties properties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player).orElse(null);
            if(properties == null)
                return;

            Identifier cosmeticId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
            ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(cosmeticId);
            if(backpack == null)
                return;

            float partialTick = state.ageInTicks - (int) state.ageInTicks; // Inferred
            BackpackRenderState backpackRenderState = new BackpackRenderState();
            ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
            backpackRenderState.bobbing = meta.bobbing();
            backpackRenderState.renderer = meta.renderer().orElse(null);
            backpackRenderState.baseModel = backpack.getBaseModel();
            backpackRenderState.strapsModel = backpack.getStrapsModel();
            backpackRenderState.entityData = LivingEntityDataState.create(player, partialTick);
            backpackRenderState.levelData = LevelDataState.create(player.level(), partialTick);
            backpackRenderState.visible = Services.BACKPACK.isBackpackVisible(player);
            backpackRenderState.cosmeticProperties = properties;
            backpackRenderState.horizontalDelta = player.getDeltaMovement().horizontalDistance();
            backpackRenderState.backpackScale = ModSyncedDataKeys.BACKPACK_SCALE.getValue(player);
            state.setData(ClientHandler.BACKPACK_RENDER_STATE_KEY, backpackRenderState);
        }
    }
}
