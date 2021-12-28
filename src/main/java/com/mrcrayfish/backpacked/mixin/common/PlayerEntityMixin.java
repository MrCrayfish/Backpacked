package com.mrcrayfish.backpacked.mixin.common;

import com.mojang.authlib.GameProfile;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements BackpackedInventoryAccess
{
    @Shadow
    @Final
    @Mutable
    public PlayerInventory inventory;

    @Shadow
    @Final
    @Mutable
    public PlayerContainer inventoryMenu;

    @Unique
    public BackpackInventory backpackedInventory = null;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void constructorTail(World world, BlockPos pos, float spawnAngle, GameProfile profile, CallbackInfo ci)
    {
        if(Backpacked.isCuriosLoaded())
            return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        this.inventory = new ExtendedPlayerInventory(player);
        this.inventoryMenu = new ExtendedPlayerContainer(this.inventory, !world.isClientSide, player);
        player.containerMenu = this.inventoryMenu;
    }

    @Override
    @Nullable
    public BackpackInventory getBackpackedInventory()
    {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = Backpacked.getBackpackStack(player);
        if(stack.isEmpty())
        {
            this.backpackedInventory = null;
            return null;
        }

        BackpackItem backpackItem = (BackpackItem) stack.getItem();
        if(this.backpackedInventory == null || !this.backpackedInventory.getBackpackStack().equals(stack) || this.backpackedInventory.getContainerSize() != backpackItem.getRowCount() * 9)
        {
            this.backpackedInventory = new BackpackInventory(backpackItem.getRowCount(), player, stack);
        }
        return this.backpackedInventory;
    }
}
