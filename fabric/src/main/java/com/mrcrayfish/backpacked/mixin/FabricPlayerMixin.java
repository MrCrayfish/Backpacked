package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class FabricPlayerMixin
{
    @Inject(method = "getProjectile", at = @At(value = "RETURN", ordinal = 3), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void backpackedLocateAmmo(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir)
    {
        Player player = (Player) (Object) this;
        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MARKSMAN.get(), backpack) <= 0)
            return;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
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
