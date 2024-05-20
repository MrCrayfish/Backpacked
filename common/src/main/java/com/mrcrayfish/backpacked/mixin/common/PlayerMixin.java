package com.mrcrayfish.backpacked.mixin.common;

import com.mojang.authlib.GameProfile;
import com.mrcrayfish.backpacked.common.backpack.impl.RocketBackpack;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class PlayerMixin implements BackpackedInventoryAccess
{
    @Shadow
    @Final
    @Mutable
    private Inventory inventory;

    @Shadow
    @Final
    @Mutable
    public InventoryMenu inventoryMenu;

    @Unique
    public BackpackInventory backpackedInventory = null;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpackedConstructorTail(Level level, BlockPos pos, float p_251702_, GameProfile profile, CallbackInfo ci)
    {
        if(Services.BACKPACK.isUsingThirdPartySlot())
            return;
        Player player = (Player) (Object) this;
        this.inventory = new ExtendedPlayerInventory(player);
        this.inventoryMenu = new ExtendedPlayerContainer(this.inventory, !level.isClientSide, player);
        player.containerMenu = this.inventoryMenu;
    }

    @Override
    @Nullable
    public BackpackInventory getBackpackedInventory()
    {
        Player player = (Player) (Object) this;
        ItemStack stack = Services.BACKPACK.getBackpackStack(player);
        if(stack.isEmpty())
        {
            this.backpackedInventory = null;
            return null;
        }

        BackpackItem backpackItem = (BackpackItem) stack.getItem();
        if(this.backpackedInventory == null || !this.backpackedInventory.getBackpackStack().equals(stack) || this.backpackedInventory.getContainerSize() != backpackItem.getRowCount() * backpackItem.getColumnCount())
        {
            this.backpackedInventory = new BackpackInventory(backpackItem.getColumnCount(), backpackItem.getRowCount(), player, stack);
        }
        return this.backpackedInventory;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 7))
    public void backpackedOnFallFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        Player player = (Player) (Object) this;
        if(!(player instanceof ServerPlayer))
            return;

        int distance = (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(RocketBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment(distance, (ServerPlayer) player);
        });
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void backpackedTickHead(CallbackInfo ci)
    {
        if(this.backpackedInventory != null)
        {
            this.backpackedInventory.tick();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    public void backpackedAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
    {
        if(this.backpackedInventory != null)
        {
            this.backpackedInventory.saveItemsToStack();
        }
    }
}
