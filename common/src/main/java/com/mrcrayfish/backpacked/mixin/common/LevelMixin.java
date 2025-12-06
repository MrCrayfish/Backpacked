package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.PlaceSoundControls;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin
{
    @Shadow
    public abstract void playSound(@Nullable Player player, double x, double y, double z, SoundEvent event, SoundSource source, float pitch, float volume);

    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$lightweaverPlaceSound(Player player, BlockPos pos, SoundEvent event, SoundSource source, float pitch, float volume, CallbackInfo ci)
    {
        Level level = (Level) (Object) this;
        if(!(level instanceof ServerLevel))
            return;

        // Return if not the targeted sound call
        if(!PlaceSoundControls.isAboutToPlay())
            return;

        // Cancels the sound if controls is marked as preventing next sound
        if(PlaceSoundControls.shouldPreventNextPlay())
        {
            ci.cancel();
            return;
        }

        // If send to all, removes the player argument which would prevent sending the sound to that player
        if(PlaceSoundControls.shouldSendToAllPlayers())
        {
            this.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, event, source, pitch, volume);
            ci.cancel();
        }
    }
}
