package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Author: MrCrayfish
 */
public class CuriosBackpack implements ICurioItem
{
    public static final CuriosBackpack INSTANCE = new CuriosBackpack();

    @NotNull
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext context, ItemStack stack)
    {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack)
    {
        return true;
    }

    @NotNull
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
        ItemStack stack2 = Services.BACKPACK.getBackpackStack(Minecraft.getInstance().player);
        System.out.println("RECIEVED UPDATE FOR HASH: " + stack.hashCode() + " - " + stack2.hashCode());
        stack.setTag(compound);
    }

    // TODO check
    /*@Override
    public boolean canEquipFromUse(SlotContext context)
    {
        // Temporary until issue is fixed: https://github.com/TheIllusiveC4/Curios/issues/332
        Optional<SlotResult> result = CuriosApi.getCuriosHelper().findCurio(context.entity(), "back", context.index());
        return result.map(slotResult -> {
            return slotResult.stack().isEmpty() || CuriosApi.getCuriosHelper().getCurio(slotResult.stack()).map(iCurio -> {
                return iCurio.canUnequip(slotResult.slotContext());
            }).orElse(true);
        }).orElse(true);
    }*/

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
        CompoundTag tag = stack.getTag();
        return tag == null || tag.getList("Items", Tag.TAG_COMPOUND).isEmpty();
    }

    @NotNull
    @Override
    public ICurio.DropRule getDropRule(SlotContext context, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack)
    {
        return Config.SERVER.backpack.keepOnDeath.get() ? ICurio.DropRule.ALWAYS_KEEP : ICurio.DropRule.DEFAULT;
    }
}
