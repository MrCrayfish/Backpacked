package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin
{
    @Inject(method = "getEntityContainer", at = @At(value = "HEAD"), cancellable = true)
    private static void backpacked$GetBackpackContainer(Level level, double x, double y, double z, CallbackInfoReturnable<Container> cir)
    {
        List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), p -> {
            return !BackpackHelper.getBackpackInventoriesWithAugment(p, ModAugmentTypes.HOPPER_BRIDGE.get()).isEmpty();
        });

        if(players.isEmpty())
            return;

        Player player = players.get(level.random.nextInt(players.size()));
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.HOPPER_BRIDGE.get());
        if(snapshots.isEmpty())
            return;

        var snapshot = snapshots.get(level.random.nextInt(snapshots.size()));
        cir.setReturnValue(snapshot.inventory());
    }

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    private static void backpacked$AddItemToBackpack(Container source, Container target, ItemStack stack, Direction face, CallbackInfoReturnable<ItemStack> cir)
    {
        if(target instanceof BackpackInventory inventory)
        {
            // Prevent unknown sources from adding items to the backpack
            if(!(source instanceof Hopper))
            {
                cir.setReturnValue(stack);
                return;
            }

            HopperBridgeAugment augment = BackpackHelper.findAugment(inventory.getBackpackStack(), ModAugmentTypes.HOPPER_BRIDGE.get());
            if(augment != null)
            {
                if(!augment.insert() || augment.filterMode().checkInsert() && !augment.isFilteringItem(stack.getItem()))
                {
                    cir.setReturnValue(stack);
                }
            }
        }
    }
}
