package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public static final Component BACKPACK_TRANSLATION = Component.translatable("container.backpack");
    public static final MutableComponent REMOVE_ITEMS_TOOLTIP = Component.translatable("backpacked.tooltip.remove_items").withStyle(ChatFormatting.RED);

    public BackpackItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag)
    {
        if(level != null)
        {
            ClientUtils.createBackpackTooltip(stack, list);
        }
    }

    public static boolean openBackpack(ServerPlayer ownerPlayer, ServerPlayer openingPlayer)
    {
        ItemStack backpack = Services.BACKPACK.getBackpackStack(ownerPlayer);
        if(!backpack.isEmpty())
        {
            BackpackInventory backpackInventory = ((BackpackedInventoryAccess) ownerPlayer).backpacked$GetBackpackInventory();
            if(backpackInventory == null)
                return false;
            BackpackItem backpackItem = (BackpackItem) backpack.getItem();
            Component title = backpack.hasCustomHoverName() ? backpack.getHoverName() : BACKPACK_TRANSLATION;
            int cols = backpackItem.getColumnCount();
            int rows = backpackItem.getRowCount();
            boolean owner = ownerPlayer.equals(openingPlayer);
            Services.BACKPACK.openBackpackScreen(openingPlayer, backpackInventory, cols, rows, owner, title);
            return true;
        }
        return false;
    }

    public int getColumnCount()
    {
        return Config.SERVER.backpack.inventorySizeColumns.get();
    }

    public int getRowCount()
    {
        return Config.SERVER.backpack.inventorySizeRows.get();
    }

    @Override
    public boolean canFitInsideContainerItems()
    {
        return false;
    }
}
