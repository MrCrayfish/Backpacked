package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.entity.ILootCapture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
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
    private List<ItemEntity> backpackedDrops;

    @Nullable
    @Override
    public List<ItemEntity> backpackedGetCapturedDrops()
    {
        return this.backpackedDrops;
    }

    @Override
    public void backpackedStartCapturingDrop()
    {
        this.backpackedDrops = new ArrayList<>();
    }

    @Override
    public void backpackedEndCapturingDrop()
    {
        this.backpackedDrops = null;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void backpackedSpawnItem(ItemStack stack, float f, CallbackInfoReturnable<ItemEntity> cir, ItemEntity itemEntity)
    {
        if(this.backpackedDrops != null)
        {
            this.backpackedDrops.add(itemEntity);
            cir.setReturnValue(itemEntity);
        }
    }
}
