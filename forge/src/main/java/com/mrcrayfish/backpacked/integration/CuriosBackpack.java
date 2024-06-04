package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.stream.StreamSupport;

/**
 * Author: MrCrayfish
 */
public class CuriosBackpack implements ICurioItem
{
    @NotNull
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext context, ItemStack stack)
    {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean canSync(SlotContext context, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack)
    {
        if(!Config.SERVER.backpack.lockIntoSlot.get())
            return true;
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        // TODO look into better checking if empty
        return contents == null || StreamSupport.stream(contents.nonEmptyItems().spliterator(), false).allMatch(ItemStack::isEmpty);
    }

    @NotNull
    @Override
    public ICurio.DropRule getDropRule(SlotContext context, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack)
    {
        return Config.SERVER.backpack.keepOnDeath.get() ? ICurio.DropRule.ALWAYS_KEEP : ICurio.DropRule.DEFAULT;
    }
}
