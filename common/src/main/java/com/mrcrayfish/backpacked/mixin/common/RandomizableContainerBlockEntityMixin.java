package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.backpack.impl.MiniChestBackpack;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.common.backpack.tracker.impl.CountProgressTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(RandomizableContainerBlockEntity.class)
public class RandomizableContainerBlockEntityMixin
{
    @Inject(method = "createMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    public void backpackedOnGenerateLoot(int windowId, Inventory playerInventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir)
    {
        if(!(player instanceof ServerPlayer))
            return;

        RandomizableContainerBlockEntity container = (RandomizableContainerBlockEntity) (Object) this;
        ResourceLocation lootTable = null; // Doesn't matter, replacing in future commit
        if(lootTable == null)
            return;

        if(!BuiltInLootTables.BURIED_TREASURE.equals(lootTable))
            return;

        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(MiniChestBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment((ServerPlayer) player);
        });
    }
}
