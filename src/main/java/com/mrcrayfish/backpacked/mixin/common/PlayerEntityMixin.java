package com.mrcrayfish.backpacked.mixin.common;

import com.mojang.authlib.GameProfile;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.RocketBackpack;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class PlayerEntityMixin implements BackpackedInventoryAccess
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
    private void constructorTail(Level world, BlockPos pos, float spawnAngle, GameProfile profile, CallbackInfo ci)
    {
        if(Backpacked.isCuriosLoaded())
            return;
        Player player = (Player) (Object) this;
        this.inventory = new ExtendedPlayerInventory(player);
        this.inventoryMenu = new ExtendedPlayerContainer(this.inventory, !world.isClientSide, player);
        player.containerMenu = this.inventoryMenu;
    }

    @Override
    @Nullable
    public BackpackInventory getBackpackedInventory()
    {
        Player player = (Player) (Object) this;
        ItemStack stack = Backpacked.getBackpackStack(player);
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
    public void onFallFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        Player player = (Player) (Object) this;
        if(!(player instanceof ServerPlayer))
            return;

        int distance = (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
        UnlockTracker.get(player).ifPresent(unlockTracker ->
        {
            unlockTracker.getProgressTracker(RocketBackpack.ID).ifPresent(progressTracker ->
            {
                CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                tracker.increment(distance, (ServerPlayer) player);
            });
        });
    }

    @Inject(method = "getProjectile", at = @At(value = "RETURN", ordinal = 3), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void locateAmmo(ItemStack stack, CallbackInfoReturnable<ItemStack> cir, Predicate<ItemStack> predicate)
    {
        Player player = (Player) (Object) this;
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MARKSMAN.get(), backpack) <= 0)
            return;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        if(inventory == null)
            return;

        ItemStack projectile = IntStream.range(0, inventory.getContainerSize())
                .mapToObj(inventory::getItem)
                .filter(predicate)
                .findFirst()
                .orElse(ItemStack.EMPTY);

        if(!projectile.isEmpty())
        {
            cir.setReturnValue(projectile);
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tickBackpacked(CallbackInfo ci)
    {
        if(this.backpackedInventory != null)
        {
            this.backpackedInventory.tick();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    public void addAdditionalSaveDataBackpacked(CompoundTag tag, CallbackInfo ci)
    {
        if(this.backpackedInventory != null)
        {
            this.backpackedInventory.saveItemsToStack();
        }
    }
}
