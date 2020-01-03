package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;
import top.theillusivec4.curios.common.capability.CapCurioItem;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
public class Curios
{
    public static ItemStack getBackpackStack(PlayerEntity player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        LazyOptional<ICurioItemHandler> optional = CuriosAPI.getCuriosHandler(player);
        optional.ifPresent(handler -> backpack.set(handler.getStackInSlot("backpacked", 0)));
        return backpack.get();
    }

    public static ICapabilityProvider createBackpackProvider()
    {
        return CapCurioItem.createProvider(new ICurio()
        {
            @Override
            public void playEquipSound(LivingEntity entity)
            {
                entity.world.playSound((PlayerEntity)null, entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, 1.0F);
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
        });
    }
}
