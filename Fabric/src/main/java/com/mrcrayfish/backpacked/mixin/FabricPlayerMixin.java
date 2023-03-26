package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.data.tracker.UnlockTracker;
import com.mrcrayfish.backpacked.entity.IUnlockTrackerHolder;
import com.mrcrayfish.backpacked.entity.LazyHolder;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
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
public class FabricPlayerMixin implements IUnlockTrackerHolder
{
    @Unique
    @Nullable
    private LazyHolder<UnlockTracker> backpackedUnlockTrackerHolder;

    @Override
    public UnlockTracker backpackedGetUnlockTracker()
    {
        if(this.backpackedUnlockTrackerHolder == null)
        {
            this.backpackedUnlockTrackerHolder = new LazyHolder<>(new CompoundTag(), UnlockTracker::new);
        }
        return this.backpackedUnlockTrackerHolder.get();
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void backpackedOnLoadUnlockTracker(CompoundTag tag, CallbackInfo ci)
    {
        this.backpackedUnlockTrackerHolder = new LazyHolder<>(tag.getCompound("BackpackedUnlockTracker"), UnlockTracker::new);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void backpackedOnSaveUnlockTracker(CompoundTag tag, CallbackInfo ci)
    {
        if(this.backpackedUnlockTrackerHolder != null)
        {
            tag.put("BackpackedUnlockTracker", this.backpackedUnlockTrackerHolder.serialize());
        }
    }

    @Inject(method = "getProjectile", at = @At(value = "RETURN", ordinal = 3), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void locateAmmo(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir)
    {
        Player player = (Player) (Object) this;
        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MARKSMAN.get(), backpack) <= 0)
            return;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        if(inventory == null)
            return;

        Predicate<ItemStack> predicate = ((ProjectileWeaponItem)itemStack.getItem()).getAllSupportedProjectiles();
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
}
