package com.mrcrayfish.backpacked.item;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.FrameworkAPI;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public static final Component BACKPACK_TRANSLATION = Component.translatable("container.backpack");
    public static final Component BACKPACK_MANAGEMENT_TRANSLATION = Component.translatable("container.backpack_management");

    public BackpackItem(Properties properties)
    {
        super(properties
            .component(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT)
            .component(ModDataComponents.UNLOCKABLE_SLOTS.get(), new UnlockableSlots(0))
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
            UnlockableSlots slots = item.getUnlockableSlots(backpack);
            Pair<Integer, Integer> data = BackpackHelper.createIndexData(ownerPlayer);
            Services.BACKPACK.openBackpackScreen(openingPlayer, inventory, cols, rows, owner, slots, data.getFirst(), data.getSecond(), title);
            return true;
        }
        openBackpackManagement(ownerPlayer);
        return false;
    }

    public static void openBackpackManagement(ServerPlayer player)
    {
        UnlockableSlots slots = BackpackHelper.getBackpackUnlockableSlots(player);
        FrameworkAPI.openMenuWithData(player, new SimpleMenuProvider((id, playerInventory, entity) -> {
            SimpleContainerData data = new SimpleContainerData(1);
            data.set(0, BackpackHelper.getFirstBackpackStack(player).isEmpty() ? 0 : 1);
            return new BackpackManagementMenu(id, player.getInventory(), new ManagementInventory(player), data, slots);
        }, BACKPACK_MANAGEMENT_TRANSLATION), new ManagementContainerData(slots));
    }

    @Nullable
    public UnlockableSlots getUnlockableSlots(ItemStack stack)
    {
        if(!stack.is(this))
            return null;

        if(Config.BACKPACK.inventory.slots.unlockAllSlots.get())
            return UnlockableSlots.ALL;

        // If missing, create the component
        UnlockableSlots slots = stack.get(ModDataComponents.UNLOCKABLE_SLOTS.get());
        if(slots == null)
        {
            slots = new UnlockableSlots(this.getColumnCount() * this.getRowCount());
            stack.set(ModDataComponents.UNLOCKABLE_SLOTS.get(), slots);
            return slots;
        }

        // Update the max slots if the size is different
        int maxSlots = this.getColumnCount() * this.getRowCount();
        if(slots.getMaxSlots() != maxSlots)
        {
            slots = slots.setMaxSlots(maxSlots);
            stack.set(ModDataComponents.UNLOCKABLE_SLOTS.get(), slots);
        }

        return slots;
    }
}
