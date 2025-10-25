package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.event.BackpackedInteractAccess;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class PlayerMixin implements BackpackedInventoryAccess
{
    @Unique
    public BackpackInventory[] backpacked$Inventory = null;

    @Unique
    private BackpackInventory[] backpacked$Inventory()
    {
        if(this.backpacked$Inventory == null)
        {
            this.backpacked$Inventory = new BackpackInventory[ManagementInventory.getMaxEquipable()];
        }
        if(this.backpacked$Inventory.length != ManagementInventory.getMaxEquipable())
        {
            BackpackInventory[] old = this.backpacked$Inventory;
            this.backpacked$Inventory = new BackpackInventory[ManagementInventory.getMaxEquipable()];
            System.arraycopy(old, 0, this.backpacked$Inventory, 0, Math.min(old.length, this.backpacked$Inventory.length));
        }
        return this.backpacked$Inventory;
    }

    @Override
    public int backpacked$GetBackpackInventoryCount()
    {
        return this.backpacked$Inventory().length;
    }

    @Override
    @Nullable
    public BackpackInventory backpacked$GetBackpackInventory(int index)
    {
        BackpackInventory[] inventories = this.backpacked$Inventory();
        if(index < 0 || index >= inventories.length)
            return null;

        Player player = (Player) (Object) this;
        ItemStack stack = BackpackHelper.getBackpackStack(player, index);
        if(stack.isEmpty())
        {
            inventories[index] = null;
            return null;
        }

        BackpackItem item = (BackpackItem) stack.getItem();
        BackpackInventory inventory = inventories[index];
        if(inventory == null || !inventory.getBackpackStack().equals(stack) || inventory.getState().isInvalid())
        {
            inventory = new BackpackInventory(index, item.getColumnCount(), item.getRowCount(), player, stack);
            inventories[index] = inventory;
        }
        return inventory;
    }

    @Override
    public Stream<BackpackInventory> backpacked$streamNonNullBackpackInventories()
    {
        Stream.Builder<BackpackInventory> builder = Stream.builder();
        for(int i = 0; i < this.backpacked$GetBackpackInventoryCount(); i++)
        {
            BackpackInventory inventory = this.backpacked$GetBackpackInventory(i);
            if(inventory != null)
            {
                builder.add(inventory);
            }
        }
        return builder.build();
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void backpacked$TickHead(CallbackInfo ci)
    {
        BackpackInventory[] inventories = this.backpacked$Inventory();
        for(BackpackInventory inventory : inventories)
        {
            if(inventory != null)
            {
                inventory.tick();
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    public void backpacked$AddAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
    {
        BackpackInventory[] inventories = this.backpacked$Inventory();
        for(BackpackInventory inventory : inventories)
        {
            if(inventory != null)
            {
                inventory.saveItemsToStack();
            }
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
