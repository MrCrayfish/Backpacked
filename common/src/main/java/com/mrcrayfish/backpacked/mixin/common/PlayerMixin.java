package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.event.BackpackedInteractAccess;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class PlayerMixin implements BackpackedInventoryAccess
{
    @Unique
    public BackpackInventory backpacked$Inventory = null;

    @Override
    @Nullable
    public BackpackInventory backpacked$GetBackpackInventory()
    {
        Player player = (Player) (Object) this;
        ItemStack stack = Services.BACKPACK.getBackpackStack(player);
        if(stack.isEmpty())
        {
            this.backpacked$Inventory = null;
            return null;
        }

        BackpackItem backpackItem = (BackpackItem) stack.getItem();
        if(this.backpacked$Inventory == null || !this.backpacked$Inventory.getBackpackStack().equals(stack) || this.backpacked$Inventory.getContainerSize() != backpackItem.getRowCount() * backpackItem.getColumnCount())
        {
            this.backpacked$Inventory = new BackpackInventory(backpackItem.getColumnCount(), backpackItem.getRowCount(), player, stack);
        }
        return this.backpacked$Inventory;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void backpacked$TickHead(CallbackInfo ci)
    {
        if(this.backpacked$Inventory != null)
        {
            this.backpacked$Inventory.tick();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    public void backpacked$AddAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
    {
        if(this.backpacked$Inventory != null)
        {
            this.backpacked$Inventory.saveItemsToStack();
        }
    }

    @Inject(method = "interactOn", at = @At(value = "HEAD"))
    public void backpacked$InteractHead(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        Player player = (Player) (Object) this;
        if(player instanceof ServerPlayer serverPlayer)
        {
            BackpackedInteractAccess access = (BackpackedInteractAccess) serverPlayer;
            List<ResourceLocation> capturedIds = access.getBackpacked$CapturedInteractIds();
            capturedIds.clear();
            ItemStack stack = serverPlayer.getItemInHand(hand);
            BackpackedEvents.INTERACTED_WITH_ENTITY_CAPTURE.post().handle(serverPlayer, stack, entity, capturedIds::add);
        }
    }
}
