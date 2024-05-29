package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Config;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class TrinketBackpackItem extends FabricBackpackItem implements Trinket
{
    public TrinketBackpackItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        return TrinketItem.equipItem(player, stack) ? InteractionResultHolder.sidedSuccess(stack, level.isClientSide()) : super.use(level, player, hand);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(!Config.SERVER.backpack.lockIntoSlot.get())
            return true;
        CompoundTag tag = stack.getTag();
        return tag == null || tag.getList("Items", Tag.TAG_COMPOUND).isEmpty();
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        entity.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return stack.getItem() instanceof BackpackItem && Config.SERVER.backpack.keepOnDeath.get() ? TrinketEnums.DropRule.KEEP : Trinket.super.getDropRule(stack, slot, entity);
    }
}
