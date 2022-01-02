package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.MiniChestBackpack;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(LockableLootTileEntity.class)
public class LockableLootTileEntityMixin
{
    @Shadow
    protected ResourceLocation lootTable;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/criterion/PlayerGeneratesContainerLootTrigger;trigger(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/util/ResourceLocation;)V"))
    public void onGenerateLoot(PlayerEntity player, CallbackInfo ci)
    {
        LockableLootTileEntity tileEntity = (LockableLootTileEntity) (Object) this;
        if(!(tileEntity instanceof ChestTileEntity))
            return;

        if(!(player instanceof ServerPlayerEntity))
            return;

        if(!LootTables.BURIED_TREASURE.equals(this.lootTable))
            return;

        UnlockTracker.get(player).ifPresent(unlockTracker ->
        {
            unlockTracker.getProgressTracker(MiniChestBackpack.ID).ifPresent(progressTracker ->
            {
                CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                tracker.increment((ServerPlayerEntity) player);
            });
        });
    }
}
