package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageShowEquipHint;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.FrameworkAPI;
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

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public static final Component BACKPACK_TRANSLATION = Component.translatable("container.backpack");
    public static final Component BACKPACK_MANAGEMENT_TRANSLATION = Component.translatable("container.backpack_management");
    public static final Component NO_MORE_BACKPACK_SLOTS_TRANSLATION = Component.translatable("backpacked.gui.no_more_backpack_slots");

    public static final String SLOTS_KEY = "Backpacked_BackpackSlots";
    public static final String AUGMENTS_KEY = "Backpacked_AugmentBays";

    public BackpackItem(Properties properties)
    {
        super(properties);
    }

    public int getColumnCount()
    {
        return Config.BACKPACK.inventory.size.columns.get();
    }

    public int getRowCount()
    {
        return Config.BACKPACK.inventory.size.rows.get();
    }

    public int getMaxAugmentBays(ItemStack stack)
    {
        return 3;
    }

    @Override
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide())
        {
            if(BackpackHelper.equipBackpack(player, stack))
            {
                Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageShowEquipHint());
                level.playSeededSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, player.getSoundSource(), 1.0F, 1.0F, player.getRandom().nextLong());
                return InteractionResultHolder.success(stack);
            }
            else
            {
                player.displayClientMessage(NO_MORE_BACKPACK_SLOTS_TRANSLATION, true);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    public static boolean openBackpack(ServerPlayer ownerPlayer, ServerPlayer openingPlayer, int backpackIndex)
    {
        BackpackInventory inventory = ((BackpackedInventoryAccess) ownerPlayer).backpacked$GetBackpackInventory(backpackIndex);
        if(inventory != null)
        {
            ItemStack backpack = inventory.getBackpackStack();
            if(!(backpack.getItem() instanceof BackpackItem item))
                return false;

            // Remember last opened backpack index
            if(Objects.equals(ownerPlayer, openingPlayer))
            {
                ModSyncedDataKeys.SELECTED_BACKPACK.setValue(ownerPlayer, backpackIndex);
            }

            Component title = backpack.hasCustomHoverName() ? backpack.getHoverName() : BACKPACK_TRANSLATION;
            int cols = item.getColumnCount();
            int rows = item.getRowCount();
            boolean owner = ownerPlayer.equals(openingPlayer);
            UnlockableSlots slots = item.getUnlockableSlots(backpack);
            Pagination pagination = BackpackHelper.createPaginationInfo(ownerPlayer, backpackIndex);
            Augments augments = Augments.cached(backpack).copy();
            UnlockableSlots bays = item.getUnlockableAugmentBays(backpack);
            Services.BACKPACK.openBackpackScreen(openingPlayer, inventory, ownerPlayer.getId(), backpackIndex, cols, rows, owner, slots, pagination, augments, title, bays);
            return true;
        }
        if(Objects.equals(ownerPlayer, openingPlayer))
        {
            openBackpackManagement(ownerPlayer, false);
        }
        return false;
    }

    public static void openBackpackManagement(ServerPlayer player, boolean showInventoryButton)
    {
        UnlockableSlots slots = BackpackHelper.getBackpackUnlockableSlots(player);
        FrameworkAPI.openMenuWithData(player, new SimpleMenuProvider((id, playerInventory, entity) -> {
            SimpleContainerData data = new SimpleContainerData(1);
            data.set(0, BackpackHelper.getFirstBackpackStack(player).isEmpty() ? 0 : 1);
            return new BackpackManagementMenu(id, player.getInventory(), new ManagementInventory(player), data, slots, showInventoryButton);
        }, BACKPACK_MANAGEMENT_TRANSLATION), buf -> {
            new ManagementContainerData(slots, showInventoryButton).encode(buf);
        });
    }

    public UnlockableSlots getUnlockableSlots(ItemStack stack)
    {
        if(Config.BACKPACK.inventory.slots.unlockAllSlots.get())
            return UnlockableSlots.all();

        // If missing, create the component
        UnlockableSlots slots = UnlockableSlots.get(stack, BackpackItem.SLOTS_KEY);

        // Update the max slots if the size is different
        int maxSlots = this.getColumnCount() * this.getRowCount();
        if(slots.getMaxSlots() != maxSlots)
        {
            slots.setMaxSlots(maxSlots);
        }

        int initialUnlocked = Config.BACKPACK.inventory.slots.initialUnlockedSlots.get();
        BackpackHelper.unlockInitialSlots(slots, initialUnlocked);

        return slots;
    }

    public UnlockableSlots getUnlockableAugmentBays(ItemStack stack)
    {
        if(Config.BACKPACK.augmentBays.unlockAllAugmentBays.get())
            return UnlockableSlots.all();

        UnlockableSlots slots = UnlockableSlots.get(stack, BackpackItem.AUGMENTS_KEY);

        // Update the max bays if the size is different
        int maxBays = this.getMaxAugmentBays(stack);
        if(slots.getMaxSlots() != maxBays)
        {
            slots.setMaxSlots(maxBays);
        }

        // Unlock the first augment bay if configured to do so
        if(Config.BACKPACK.augmentBays.unlockFirstAugmentBay.get())
        {
            if(!slots.isUnlocked(0))
            {
                slots.unlockSlot(0);
            }
        }

        return slots;
    }
}
