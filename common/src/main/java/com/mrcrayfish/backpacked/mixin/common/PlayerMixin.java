package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.MovementType;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.event.BackpackedInteractAccess;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
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

    @Unique
    private void backpacked$PlayerTravelEvent(double dx, double dy, double dz, MovementType type)
    {
        Player player = (Player) (Object) this;
        if(player.level().isClientSide())
            return;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        BackpackedEvents.PLAYER_TRAVEL.post().handle((ServerPlayer) player, distanceSquared, type);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 0))
    private void backpacked$MovementSwim(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.SWIM);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 1))
    private void backpacked$MovementWalkUnderwater(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.WALK_UNDERWATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 2))
    private void backpacked$MovementWalkOnWater(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.WALK_ON_WATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 3))
    private void backpacked$MovementClimb(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(0, dy, 0, MovementType.CLIMB);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 4))
    private void backpacked$MovementSprint(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.SPRINT);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 5))
    private void backpacked$MovementSneak(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.SNEAK);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 6))
    private void backpacked$MovementWalk(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.WALK);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 7))
    private void backpacked$MovementElytraFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.ELYTRA_FLY);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 8))
    private void backpacked$MovementFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.FLY);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 8))
    private void backpacked$MovementFall(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(0, dy, 0, MovementType.FALL);
    }

    @Inject(method = "checkRidingStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getVehicle()Lnet/minecraft/world/entity/Entity;", ordinal = 0))
    private void backpacked$MovementVehicle(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.VEHICLE);
    }
}
