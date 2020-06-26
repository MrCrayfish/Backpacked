package com.mrcrayfish.backpacked.integration;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Author: MrCrayfish
 */
public class Curios
{
    public static ItemStack getBackpackStack(PlayerEntity player)
    {
        /*AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        LazyOptional<ICurioItemHandler> optional = CuriosAPI.getCuriosHandler(player);
        optional.ifPresent(handler -> backpack.set(handler.getStackInSlot("backpacked", 0)));
        return backpack.get();*/
        return ItemStack.EMPTY;
    }

    public static ICapabilityProvider createBackpackProvider()
    {
        /*return CapCurioItem.createProvider(new ICurio()
        {
            @Override
            public void playEquipSound(LivingEntity entity)
            {
                entity.world.playSound((PlayerEntity)null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            @Override
            public boolean canRightClickEquip()
            {
                return true;
            }

            @Override
            public boolean shouldSyncToTracking(String identifier, LivingEntity livingEntity)
            {
                return true;
            }

            @Nonnull
            @Override
            public DropRule getDropRule(LivingEntity livingEntity)
            {
                return Config.COMMON.keepBackpackOnDeath.get() ? DropRule.ALWAYS_KEEP : DropRule.DEFAULT;
            }
        });*/
        return null;
    }
}
