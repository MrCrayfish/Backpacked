package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class FabricPlayerMixin
{
    @Inject(method = "getProjectile", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    public void backpackedLocateAmmo(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir)
    {
        if(weapon.getItem() instanceof ProjectileWeaponItem item)
        {
            Player player = (Player) (Object) this;
            BackpackedInventoryAccess access = (BackpackedInventoryAccess) player;
            for(int i = 0; i < access.backpacked$GetBackpackInventoryCount(); i++)
            {
                BackpackInventory inventory = access.backpacked$GetBackpackInventory(i);
                if(inventory == null)
                    continue;

                ItemStack backpack = inventory.getBackpackStack();
                HolderLookup<Enchantment> lookup = player.level().holderLookup(Registries.ENCHANTMENT);
                if(EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(ModEnchantments.MARKSMAN), backpack) <= 0)
                    continue;

                Predicate<ItemStack> predicate = item.getSupportedHeldProjectiles();
                ItemStack projectile = InventoryHelper.streamFor(inventory).filter(predicate).findFirst().orElse(ItemStack.EMPTY);
                if(projectile.isEmpty())
                    continue;

                cir.setReturnValue(projectile);
                break;
            }
        }
    }

    @Inject(method = "dropEquipment", at = @At(value = "TAIL"))
    private void backpacked$DropBackpack(CallbackInfo ci)
    {
        Player player = (Player) (Object) this;
        if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
            return;

        if(Config.BACKPACK.keepOnDeath.get())
            return;

        BackpackHelper.removeAllBackpacks(player).forEach(stack -> {
            if(!stack.isEmpty()) {
                player.drop(stack, true, false);
            }
        });
    }
}
