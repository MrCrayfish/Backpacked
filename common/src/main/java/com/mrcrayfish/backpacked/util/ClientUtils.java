package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientUtils
{
    public static void createBackpackTooltip(ItemStack stack, List<Component> list)
    {
        if(!Config.SERVER.backpack.lockIntoSlot.get())
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null && Services.BACKPACK.getBackpackStack(mc.player).equals(stack))
        {
            CompoundTag tag = stack.getTag();
            if(tag != null && !tag.getList("Items", Tag.TAG_COMPOUND).isEmpty())
            {
                mc.font.getSplitter().splitLines(BackpackItem.REMOVE_ITEMS_TOOLTIP, 150, Style.EMPTY).forEach(formattedText -> {
                    list.add(Component.literal(formattedText.getString()).withStyle(ChatFormatting.RED));
                });
            }
        }
    }
}
