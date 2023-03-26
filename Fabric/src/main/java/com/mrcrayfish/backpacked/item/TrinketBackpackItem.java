package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Config;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Author: MrCrayfish
 */
public class TrinketBackpackItem extends BackpackItem implements Trinket
{
    public TrinketBackpackItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand)
    {
        ItemStack stack = user.getItemInHand(hand);
        return TrinketItem.equipItem(user, stack) ? InteractionResultHolder.sidedSuccess(stack, world.isClientSide()) : super.use(world, user, hand);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(!Config.SERVER.common.lockBackpackIntoSlot.get())
            return true;
        CompoundTag tag = stack.getTag();
        return tag == null || tag.getList("Items", Tag.TAG_COMPOUND).isEmpty();
    }

    @Nullable
    @Override
    public SoundEvent getEquipSound()
    {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return stack.getItem() instanceof BackpackItem && Config.COMMON.common.keepBackpackOnDeath.get() ? TrinketEnums.DropRule.KEEP : Trinket.super.getDropRule(stack, slot, entity);
    }
}
