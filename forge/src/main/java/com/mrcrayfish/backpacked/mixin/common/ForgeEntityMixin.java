package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.impl.ImbuedHideAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ForgeEntityMixin
{
    @Inject(method = "isInvulnerableTo", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$InvulnerabilityCheck(DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        if(this.backpacked$CheckEntityForInvulnerability((Entity) (Object) this, source))
        {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean backpacked$CheckEntityForInvulnerability(Entity entity, DamageSource source)
    {
        if(entity.getType() == EntityType.ITEM)
        {
            ItemEntity itemEntity = ((ItemEntity) entity);
            ItemStack stack = itemEntity.getItem();
            if(stack.is(ModItems.BACKPACK.get()))
            {
                ImbuedHideAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.IMBUED_HIDE.get());
                if(augment != null)
                {
                    return this.backpacked$isImbuedHideImmuneToDamageSource(itemEntity.level().registryAccess(), source);
                }
            }
        }
        return false;
    }

    @Unique
    private boolean backpacked$isImbuedHideImmuneToDamageSource(RegistryAccess access, DamageSource source)
    {
        Registry<DamageType> types = access.registryOrThrow(Registries.DAMAGE_TYPE);
        ResourceLocation key = types.getKey(source.type());
        if(key != null)
        {
            return Config.AUGMENTS.imbuedHide.invulnerableToDamageTypes.get().contains(key.toString());
        }
        return false;
    }
}
