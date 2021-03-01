package com.mrcrayfish.backpacked.mixin.common;

import com.mojang.authlib.GameProfile;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @Shadow
    @Final
    @Mutable
    public PlayerInventory inventory;

    @Shadow
    @Final
    @Mutable
    public PlayerContainer container;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void constructorTail(World world, BlockPos pos, float spawnAngle, GameProfile profile, CallbackInfo ci)
    {
        if(Backpacked.isCuriosLoaded())
            return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        this.inventory = new ExtendedPlayerInventory(player);
        this.container = new ExtendedPlayerContainer(this.inventory, !world.isRemote, player);
        player.openContainer = this.container;
    }
}
