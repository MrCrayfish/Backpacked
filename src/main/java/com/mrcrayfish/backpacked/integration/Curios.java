package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.capability.CurioItemCapability;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
public class Curios
{
    public static ItemStack getBackpackStack(PlayerEntity player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        optional.ifPresent(itemHandler ->
        {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(SlotTypePreset.BACK.getIdentifier());
            stacksOptional.ifPresent(stacksHandler ->
            {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(0);
                if(stack.getItem() instanceof BackpackItem)
                {
                    backpack.set(stack);
                }
            });
        });
        return backpack.get();
    }

    public static boolean isBackpackVisible(PlayerEntity player)
    {
        AtomicReference<Boolean> visible = new AtomicReference<>(true);
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        optional.ifPresent(itemHandler -> {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(SlotTypePreset.BACK.getIdentifier());
            stacksOptional.ifPresent(stacksHandler -> {
                visible.set(stacksHandler.getRenders().get(0));
            });
        });
        return visible.get();
    }

    public static ICapabilityProvider createBackpackProvider()
    {
        return CurioItemCapability.createProvider(new ICurio()
        {
            @Override
            public void playRightClickEquipSound(LivingEntity entity)
            {
                entity.world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            @Override
            public boolean canRightClickEquip()
            {
                return true;
            }

            @Override
            public boolean canSync(String identifier, int index, LivingEntity livingEntity)
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
