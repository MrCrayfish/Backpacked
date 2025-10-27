package com.mrcrayfish.backpacked.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.entity.LootCapture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(Entity.class)
public class EntityMixin implements LootCapture
{
    @Unique
    private WeakReference<ServerPlayer> backpacked$killedByPlayer;

    @Override
    public void backpacked$StartCapturingDrop(ServerPlayer player)
    {
        this.backpacked$killedByPlayer = new WeakReference<>(player);
    }

    @Override
    public void backpacked$EndCapturingDrop()
    {
        this.backpacked$killedByPlayer = null;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"), cancellable = true)
    private void backpacked$SpawnItem(ItemStack stack, float f, CallbackInfoReturnable<ItemEntity> cir, @Local(ordinal = 0) ItemEntity itemEntity)
    {
        if(this.backpacked$killedByPlayer != null)
        {
            ServerPlayer player = this.backpacked$killedByPlayer.get();
            if(player != null)
            {
                List<ItemEntity> drops = new ArrayList<>(1);
                drops.add(itemEntity);
                AugmentHandler.onLootDroppedByEntity(drops, player);
                if(drops.isEmpty())
                {
                    cir.setReturnValue(null);
                }
            }
            else
            {
                this.backpacked$killedByPlayer = null;
            }
        }
    }
}
