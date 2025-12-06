package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.PlaceSoundControls;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin
{
    /* Marks in PlaceSoundControls just before the block place sound is about to play so it can
     * correctly apply the modifications in PlaceSoundControls */
    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 0))
    private void backpacked$aboutToPlayPlaceSound(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir)
    {
        PlaceSoundControls.markAboutToPlay();
    }
}
