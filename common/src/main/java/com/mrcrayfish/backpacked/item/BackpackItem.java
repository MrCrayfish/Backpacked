package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.BackpackHelper;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public static final Component BACKPACK_TRANSLATION = Component.translatable("container.backpack");
    public static final Component BACKPACK_MANAGEMENT_TRANSLATION = Component.translatable("container.backpack_management");
    private static final AtomicBoolean OPENING_MANAGEMENT = new AtomicBoolean(false);

    public BackpackItem(Properties properties)
    {
        super(properties
            .component(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT)
            .component(ModDataComponents.UNLOCKED_SLOTS.get(), new UnlockedSlots(0))
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide())
        {
            if(BackpackHelper.equipBackpack(player, stack))
            {
                level.playSeededSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), player.getSoundSource(), 1.0F, 1.0F, player.getRandom().nextLong());
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    public int getColumnCount()
    {
        return Config.BACKPACK.inventory.size.columns.get();
    }

    public int getRowCount()
    {
        return Config.BACKPACK.inventory.size.rows.get();
    }

    @Override
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    public static boolean openBackpack(ServerPlayer ownerPlayer, ServerPlayer openingPlayer)
    {
        // Fixes an issue when opening management, the slot listener tries to reopen the backpack
        if(OPENING_MANAGEMENT.get())
            return false;

        int selected = BackpackHelper.getSelectedBackpackIndex(ownerPlayer);
        BackpackInventory inventory = ((BackpackedInventoryAccess) ownerPlayer).backpacked$GetBackpackInventory(selected);
        if(inventory != null)
        {
            ItemStack backpack = inventory.getBackpackStack();
            if(!(backpack.getItem() instanceof BackpackItem item))
                return false;

            Component title = backpack.has(DataComponents.CUSTOM_NAME) ? backpack.getHoverName() : BACKPACK_TRANSLATION;
            int cols = item.getColumnCount();
            int rows = item.getRowCount();
            boolean owner = ownerPlayer.equals(openingPlayer);
            UnlockedSlots slots = item.getUnlockedSlots(backpack);
            Services.BACKPACK.openBackpackScreen(openingPlayer, inventory, cols, rows, owner, slots, title);
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
        }, BACKPACK_MANAGEMENT_TRANSLATION));
        OPENING_MANAGEMENT.set(false);
    }

    @Nullable
    public UnlockedSlots getUnlockedSlots(ItemStack stack)
    {
        if(!stack.is(this))
            return null;

        if(Config.BACKPACK.inventory.slots.unlockAllSlots.get())
            return UnlockedSlots.ALL;

        // If missing, create the component
        UnlockedSlots slots = stack.get(ModDataComponents.UNLOCKED_SLOTS.get());
        if(slots == null)
        {
            slots = new UnlockedSlots(this.getColumnCount() * this.getRowCount());
            stack.set(ModDataComponents.UNLOCKED_SLOTS.get(), slots);
            return slots;
        }

        // Update the max slots if the size is different
        int maxSlots = this.getColumnCount() * this.getRowCount();
        if(slots.getMaxSlots() != maxSlots)
        {
            slots = slots.setMaxSlots(maxSlots);
            stack.set(ModDataComponents.UNLOCKED_SLOTS.get(), slots);
        }

        return slots;
    }
}
