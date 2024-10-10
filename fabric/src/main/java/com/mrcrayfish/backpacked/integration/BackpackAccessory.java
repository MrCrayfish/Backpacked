package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;

import java.util.stream.StreamSupport;

/**
 * Author: MrCrayfish
 */
public class BackpackAccessory implements Accessory
{
    @Override
    @Nullable
    public SoundEventData getEquipSound(ItemStack stack, SlotReference reference)
    {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack)
    {
        return true;
    }

    // TODO do I need this?
    /*@NotNull
    @Override
    public CompoundTag writeSyncData(SlotContext context, ItemStack stack)
    {
        // TODO dont send full inventory to other players, this isn't diablo 4
        return stack.getOrCreateTag();
    }

    @Override
    public void readSyncData(SlotContext context, CompoundTag compound, ItemStack stack)
    {
        if(context.cosmetic())
            return;
        stack.setTag(compound);
    }*/

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference)
    {
        if(!Config.SERVER.backpack.lockIntoSlot.get())
            return true;
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        // TODO look into better checking if empty
        return contents == null || StreamSupport.stream(contents.nonEmptyItems().spliterator(), false).allMatch(ItemStack::isEmpty);
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source)
    {
        return Config.SERVER.backpack.keepOnDeath.get() ? DropRule.KEEP : DropRule.DEFAULT;
    }
}
