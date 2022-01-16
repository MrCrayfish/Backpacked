package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.capability.CurioItemCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public static void setBackpackStack(PlayerEntity player, ItemStack stack)
    {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        optional.ifPresent(itemHandler ->
        {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(SlotTypePreset.BACK.getIdentifier());
            stacksOptional.ifPresent(stacksHandler ->
            {
                stacksHandler.getStacks().setStackInSlot(0, stack.copy());
            });
        });
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

    public static ICapabilityProvider createBackpackProvider(ItemStack stack)
    {
        return CurioItemCapability.createProvider(new ICurio()
        {
            @Nonnull
            @Override
            public SoundInfo getEquipSound(SlotContext slotContext)
            {
                return new SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            }

            @Override
            public boolean canEquipFromUse(SlotContext slotContext)
            {
                return true;
            }

            @Override
            public boolean canUnequip(String identifier, LivingEntity livingEntity)
            {
                if(!Config.SERVER.lockBackpackIntoSlot.get())
                    return true;
                CompoundNBT tag = stack.getTag();
                return tag == null || tag.getList("Items", Constants.NBT.TAG_COMPOUND).isEmpty();
            }

            @Override
            public boolean canSync(String identifier, int index, LivingEntity livingEntity)
            {
                return true;
            }

            @Nullable
            @Override
            public CompoundNBT writeSyncData()
            {
                CompoundNBT realTag = stack.getOrCreateTag();
                CompoundNBT tag = new CompoundNBT();
                tag.putString("BackpackModel", stack.getOrCreateTag().getString("BackpackModel"));
                for(BackpackModelProperty property : BackpackModelProperty.values())
                {
                    String tagName = property.getTagName();
                    boolean value = realTag.contains(tagName, Constants.NBT.TAG_BYTE) ? realTag.getBoolean(tagName) : property.getDefaultValue();
                    tag.putBoolean(tagName, value);
                }
                return tag;
            }

            @Override
            public void readSyncData(CompoundNBT compound)
            {
                stack.getOrCreateTag().putString("BackpackModel", compound.getString("BackpackModel"));
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
