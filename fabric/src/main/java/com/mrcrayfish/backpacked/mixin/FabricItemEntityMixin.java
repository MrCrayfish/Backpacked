package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public class FabricItemEntityMixin
{
    @Shadow
    @Nullable
    private UUID target;

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0), cancellable = true)
    private void backpacked$BeforePickup(Player player, CallbackInfo ci)
    {
        ItemEntity entity = (ItemEntity) (Object) this;
        if(AugmentHandler.beforeItemPickup(player, entity, this.target))
            ci.cancel();
    }
}
