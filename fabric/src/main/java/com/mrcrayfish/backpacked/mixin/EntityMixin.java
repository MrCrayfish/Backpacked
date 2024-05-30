package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.entity.ILootCapture;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(Entity.class)
public class EntityMixin implements ILootCapture
{
    @Unique
    private List<ItemEntity> backpacked$Drops;

    @Nullable
    @Override
    public List<ItemEntity> backpacked$GetCapturedDrops()
    {
        return this.backpacked$Drops;
    }

    @Override
    public void backpacked$StartCapturingDrop()
    {
        this.backpacked$Drops = new ArrayList<>();
    }

    @Override
    public void backpacked$EndCapturingDrop()
    {
        this.backpacked$Drops = null;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void backpacked$SpawnItem(ItemStack stack, float f, CallbackInfoReturnable<ItemEntity> cir, ItemEntity itemEntity)
    {
        if(this.backpacked$Drops != null)
        {
            this.backpacked$Drops.add(itemEntity);
            cir.setReturnValue(itemEntity);
        }
    }
}
