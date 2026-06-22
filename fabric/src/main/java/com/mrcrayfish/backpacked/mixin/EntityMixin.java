package com.mrcrayfish.backpacked.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.augment.impl.ImbuedHideAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.entity.LootCapture;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(Entity.class)
public class EntityMixin implements LootCapture
{
    @Unique
    private WeakReference<ServerPlayer> backpacked$killedByPlayer;

    @Override
    public void backpacked$StartCapturingDrop(ServerPlayer player)
    {
        this.backpacked$killedByPlayer = new WeakReference<>(player);
    }

    @Override
    public void backpacked$EndCapturingDrop()
    {
        this.backpacked$killedByPlayer = null;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"), cancellable = true)
    private void backpacked$SpawnItem(ServerLevel serverLevel, ItemStack itemStack, Vec3 vec3, CallbackInfoReturnable<ItemEntity> cir, @Local(ordinal = 0) ItemEntity itemEntity)
    {
        if(this.backpacked$killedByPlayer != null)
        {
            ServerPlayer player = this.backpacked$killedByPlayer.get();
            if(player != null)
            {
                List<ItemEntity> drops = new ArrayList<>(1);
                drops.add(itemEntity);
                AugmentHandler.onLootDroppedByEntity(drops, player);
                if(drops.isEmpty())
                {
                    cir.setReturnValue(null);
                }
            }
            else
            {
                this.backpacked$killedByPlayer = null;
            }
        }
    }

    @ModifyReturnValue(method = "isInvulnerableToBase", at = @At(value = "RETURN"))
    private boolean backpacked$InvulnerabilityCheck(boolean original, @Local(ordinal = 0, argsOnly = true) DamageSource source)
    {
        return original || this.backpacked$CheckEntityForInvulnerability((Entity) (Object) this, source);
    }

    @Unique
    private boolean backpacked$CheckEntityForInvulnerability(Entity entity, DamageSource source)
    {
        if(entity.getType() == EntityTypes.ITEM)
        {
            ItemEntity itemEntity = ((ItemEntity) entity);
            ItemStack stack = itemEntity.getItem();
            if(stack.is(ModItems.BACKPACK.get()))
            {
                ImbuedHideAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.IMBUED_HIDE.get());
                if(augment != null)
                {
                    return this.backpacked$isImbuedHideImmuneToDamageSource(itemEntity.registryAccess(), source);
                }
            }
        }
        return false;
    }

    @Unique
    private boolean backpacked$isImbuedHideImmuneToDamageSource(RegistryAccess access, DamageSource source)
    {
        Registry<DamageType> types = access.lookupOrThrow(Registries.DAMAGE_TYPE);
        Identifier key = types.getKey(source.type());
        if(key != null)
        {
            return Config.AUGMENTS.imbuedHide.invulnerableToDamageTypes.get().contains(key.toString());
        }
        return false;
    }
}
