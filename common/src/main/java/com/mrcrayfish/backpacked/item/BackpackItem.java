package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.inventory.container.OnPlacedBackpackListener;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public static final Component BACKPACK_TRANSLATION = Component.translatable("container.backpack");
    public static final MutableComponent REMOVE_ITEMS_TOOLTIP = Component.translatable("backpacked.tooltip.remove_items").withStyle(ChatFormatting.RED);
    private static final AtomicBoolean OPENING_MANAGEMENT = new AtomicBoolean(false);

    public BackpackItem(Properties properties)
    {
        super(properties
            .component(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT)
            .component(ModDataComponents.UNLOCKED_SLOTS.get(), UnlockedSlots.EMPTY)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag)
    {
        if(context != TooltipContext.EMPTY)
        {
            ClientUtils.createBackpackTooltip(stack, list);
        }
    }

    public static boolean openBackpack(ServerPlayer ownerPlayer, ServerPlayer openingPlayer)
    {
        // Fixes an issue when opening management, the slot listener tries to reopen the backpack
        if(OPENING_MANAGEMENT.get())
            return false;

        ItemStack backpack = Services.BACKPACK.getBackpackStack(ownerPlayer);
        if(!backpack.isEmpty())
        {
            BackpackInventory backpackInventory = ((BackpackedInventoryAccess) ownerPlayer).backpacked$GetBackpackInventory();
            if(backpackInventory == null)
                return false;
            BackpackItem backpackItem = (BackpackItem) backpack.getItem();
            Component title = backpack.has(DataComponents.CUSTOM_NAME) ? backpack.getHoverName() : BACKPACK_TRANSLATION;
            int cols = backpackItem.getColumnCount();
            int rows = backpackItem.getRowCount();
            boolean owner = ownerPlayer.equals(openingPlayer);
            UnlockedSlots slots = backpack.get(ModDataComponents.UNLOCKED_SLOTS.get());
            Services.BACKPACK.openBackpackScreen(openingPlayer, backpackInventory, cols, rows, owner, slots, title);
            return true;
        }
        openBackpackManagement(ownerPlayer);
        return false;
    }

    public static void openBackpackManagement(ServerPlayer player)
    {
        OPENING_MANAGEMENT.set(true);
        player.openMenu(new SimpleMenuProvider((windowId, inventory, player1) -> {
            BackpackManagementMenu menu = new BackpackManagementMenu(windowId, inventory, new ManagementInventory(player));
            menu.addSlotListener(new OnPlacedBackpackListener());
            return menu;
        }, Component.literal("Hello")));
        OPENING_MANAGEMENT.set(false);
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
