package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.client.ClientHandler;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(EntityModelSet.class)
public class EntityModelSetMixin
{
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onResourceManagerReload", at = @At(value = "TAIL"))
    private void backpackedBakeBackpackModels(ResourceManager manager, CallbackInfo ci)
    {
        ClientHandler.bakeBackpackModels((EntityModelSet) (Object) this);
    }
}
