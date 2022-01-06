package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
    public static ItemStack getBackpackStack(Player player)
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

    public static void setBackpackStack(Player player, ItemStack stack)
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

    public static boolean isBackpackVisible(Player player)
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
            @Override
            public ItemStack getStack()
            {
                return stack;
            }

            @Nonnull
            @Override
            public SoundInfo getEquipSound(SlotContext context)
            {
                return new SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            }

            @Override
            public boolean canEquipFromUse(SlotContext context)
            {
                return true;
            }

            @Override
            public boolean canSync(SlotContext context)
            {
                return true;
            }

            @Override
            public boolean canUnequip(SlotContext context)
            {
                if(!Config.SERVER.lockBackpackIntoSlot.get())
                    return true;
                CompoundTag tag = stack.getTag();
                return tag == null || tag.getList("Items", Tag.TAG_COMPOUND).isEmpty();
            }

            @Nullable
            @Override
            public CompoundTag writeSyncData(SlotContext context)
            {
                CompoundTag realTag = stack.getOrCreateTag();
                CompoundTag tag = new CompoundTag();
                tag.putString("BackpackModel", stack.getOrCreateTag().getString("BackpackModel"));
                for(BackpackModelProperty property : BackpackModelProperty.values())
                {
                    String tagName = property.getTagName();
                    boolean value = realTag.contains(tagName, Tag.TAG_BYTE) ? realTag.getBoolean(tagName) : property.getDefaultValue();
                    tag.putBoolean(tagName, value);
                }
                return tag;
            }

            @Override
            public void readSyncData(SlotContext context, CompoundTag compound)
            {
                CompoundTag itemTag = stack.getOrCreateTag();
                itemTag.putString("BackpackModel", compound.getString("BackpackModel"));
                for(BackpackModelProperty property : BackpackModelProperty.values())
                {
                    String tagName = property.getTagName();
                    boolean value = compound.contains(tagName, Tag.TAG_BYTE) ? compound.getBoolean(tagName) : property.getDefaultValue();
                    itemTag.putBoolean(tagName, value);
                }
            }

            @Nonnull
            @Override
            public DropRule getDropRule(SlotContext context, DamageSource source, int lootingLevel, boolean recentlyHit)
            {
                return Config.COMMON.keepBackpackOnDeath.get() ? DropRule.ALWAYS_KEEP : DropRule.DEFAULT;
            }
        });
    }
}
