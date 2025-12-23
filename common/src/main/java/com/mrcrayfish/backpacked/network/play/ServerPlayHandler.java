package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.CreativeCategorySort;
import com.mrcrayfish.backpacked.common.ItemSorting;
import com.mrcrayfish.backpacked.common.ShelfKey;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.SavedAugments;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModRegistries;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.SyncUnlockableSlots;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;

import java.util.*;

/**
 * Author: MrCrayfish
 */
public class ServerPlayHandler
{
    public static void handleCustomiseBackpack(MessageBackpackCosmetics message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(player == null)
            return;

        ItemStack stack = BackpackHelper.getBackpackStack(player, message.backpackIndex());
        if(stack.isEmpty())
            return;

        CosmeticProperties properties = message.properties();
        Optional<ResourceLocation> cosmeticOptional = properties.cosmetic();
        if(cosmeticOptional.isPresent())
        {
            ResourceLocation cosmetic = cosmeticOptional.get();
            Backpack backpack = BackpackManager.instance().getBackpack(cosmetic);
            if(backpack == null)
                return;

            if(!backpack.isUnlocked(player) && !Config.BACKPACK.cosmetics.unlockAllCosmetics.get())
                return;
        }

        stack.set(ModDataComponents.COSMETIC_PROPERTIES.get(), properties);
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer opener))
            return;

        int backpackIndex = message.backpackIndex();
        if(backpackIndex == -1)
        {
            backpackIndex = BackpackHelper.getSelectedBackpackIndex(opener);
        }

        BackpackItem.openBackpack(opener, opener, backpackIndex);
    }

    public static void handlePickpocketBackpack(MessagePickpocketBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer opener))
            return;

        // Otherwise opener is trying to open another entity's backpack
        Entity entity = opener.level().getEntity(message.entityId());
        if(!(entity instanceof LivingEntity target))
            return;

        // TODO make pickpocketing a backpack setting, not a config option
        if(!Config.PICKPOCKETING.enabled.get() && target instanceof ServerPlayer)
            return;

        // The opener should not be able to open to backpack if not in proximity and reach range
        if(!PickpocketUtil.canSeeBackpack(target, opener))
            return;

        if(target instanceof ServerPlayer targetPlayer)
        {
            int backpackIndex = BackpackHelper.firstAvailableBackpackIndex(targetPlayer);
            if(BackpackItem.openBackpack(targetPlayer, opener, backpackIndex))
            {
                targetPlayer.displayClientMessage(Component.translatable("message.backpacked.player_opened"), true);
                player.level().playSound(player, targetPlayer.getX(), targetPlayer.getY() + 1.0, targetPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 0.75F, 1.0F);
            }
        }
        else if(target instanceof WanderingTrader trader)
        {
            WanderingTraderEvents.openBackpack(trader, opener);
        }
    }

    public static void handleNavigateBackpack(MessageNavigateBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer opener))
            return;

        if(!(opener.containerMenu instanceof BackpackContainerMenu menu))
            return;

        // Navigation is only available if backpack is equipped on a player
        int ownerId = menu.getOwnerId();
        if(ownerId < 0 || !(opener.level().getEntity(ownerId) instanceof ServerPlayer target))
            return;

        // Don't navigate if the backpack index is invalid
        int backpackIndex = menu.getBackpackIndex();
        if(backpackIndex < 0 || backpackIndex >= Config.BACKPACK.equipable.maxEquipable.get())
            return;

        // If opener and target are different, revalidate that they can see the backpack
        if(!Objects.equals(opener, target) && !PickpocketUtil.canSeeBackpack(target, opener))
        {
            opener.closeContainer();
            return;
        }

        // Only navigate if the next backpack index is different
        backpackIndex = BackpackHelper.navigateBackpackIndex(target, backpackIndex, message.navigate());
        if(menu.getBackpackIndex() == backpackIndex)
            return;

        BackpackItem.openBackpack(target, opener, backpackIndex);
    }

    public static void handleRequestCustomisation(MessageRequestCustomisation message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        if(Config.BACKPACK.cosmetics.disableCustomisation.get())
            return;

        ItemStack stack = BackpackHelper.getBackpackStack(serverPlayer, message.backpackIndex());
        if(stack.isEmpty())
            return;

        boolean showCosmeticWarning = BackpackHelper.getFirstBackpackStack(serverPlayer) != stack;
        Map<ResourceLocation, Component> labelMap = new HashMap<>();
        Map<ResourceLocation, Double> completionMap = new HashMap<>();
        UnlockManager.getTracker(player).ifPresent(unlockTracker -> {
            for(Backpack backpack : BackpackManager.instance().getBackpacks()) {
                if(!unlockTracker.isUnlocked(backpack.getId())) {
                    unlockTracker.getProgressTracker(backpack.getId()).ifPresent(progressTracker -> {
                        labelMap.put(backpack.getId(), progressTracker.getDisplayComponent());
                        completionMap.put(backpack.getId(), progressTracker.getCompletionProgress());
                    });
                }
            }
        });
        CosmeticProperties properties = stack.getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
        serverPlayer.closeContainer();
        Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageOpenCustomisation(message.backpackIndex(), labelMap, properties, showCosmeticWarning, completionMap));
    }

    public static void handleRequestManagement(MessageRequestManagement message, MessageContext context)
    {
        context.getPlayer().ifPresent(player -> {
            if(player.containerMenu instanceof BackpackContainerMenu menu) {
                menu.openManagement((ServerPlayer) player);
            }
        });
    }

    @SuppressWarnings("ConstantValue")
    public static void handleUnlockSlot(MessageUnlockSlot message, MessageContext context)
    {
        context.getPlayer().ifPresent(player ->
        {
            if(!(player instanceof ServerPlayer))
                return;

            AbstractContainerMenu menu = player.containerMenu;
            if(menu == null || !menu.stillValid(player))
                return;

            for(int slotIndex : message.slotIndexes()) {
                // Player should not be sending out of bounds indexes
                if(slotIndex < 0 || slotIndex >= menu.slots.size()) {
                    // This will boot them
                    throw new IllegalArgumentException("Invalid slot index: " + slotIndex);
                }
            }

            List<UnlockableSlot> changed = new ArrayList<>();
            for(int slotIndex : message.slotIndexes()) {
                if(menu.getSlot(slotIndex) instanceof UnlockableSlot slot) {
                    if(slot.unlock(player)) {
                        changed.add(slot);
                    }
                }
            }

            if(changed.isEmpty())
                return;

            // Find distinct containers and mark as changed
            changed.stream().map(slot -> slot.container).distinct().forEach(Container::setChanged);

            // Finally sync the changes to the player. If menu has custom sync handling, call that instead.
            if(menu instanceof SyncUnlockableSlots) {
                ((SyncUnlockableSlots) menu).handleSyncSlots((ServerPlayer) player, changed);
            } else {
                List<Integer> slotIndexes = changed.stream().map(slot -> slot.index).toList();
                Network.PLAY.sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockSlot(slotIndexes));
            }
        });
    }

    public static void handleChangeAugment(MessageChangeAugment message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container and must be the wearer
        if(!(serverPlayer.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner())
            return;

        // Only works if in an equipped backpack, not a shelf
        if(!(menu.getBackpackInventory() instanceof BackpackInventory))
            return;

        // Check if valid stack at the given backpack index
        int backpackIndex = menu.getBackpackIndex();
        ItemStack stack = BackpackHelper.getBackpackStack(serverPlayer, backpackIndex);
        if(stack.isEmpty())
            return;

        // TODO validate if has unlocked augments, also validate augment settings, sync augments on fail
        // TODO add events to aguments when they are added and removed so they can add/remove data like components (e.g. fluid tank)

        AugmentType<?> type = ModRegistries.AUGMENT_TYPES.getValue(message.augmentTypeId());
        if(type == null)
            throw new IllegalArgumentException("Player sent an invalid augment type");

        // Prevent changing to an augment that is disabled
        if(Config.getDisabledAugments().contains(type.id()))
            return;

        Augments currentAugments = Augments.get(stack);
        SavedAugments savedAugments = SavedAugments.get(stack);

        // Augments must be unique except for empty type
        if(!type.isEmpty() && currentAugments.has(type))
            return;

        // Don't update if the augment is the same
        Augment<?> updatedAugment = savedAugments.getSavedOrCreateDefault(type);
        Augment<?> currentAugment = currentAugments.getAugment(message.position());
        if(updatedAugment.equals(currentAugment))
            return;

        // If the current augment has settings, save them so it can be restored later
        if(currentAugment.type() != updatedAugment.type())
            savedAugments = savedAugments.add(currentAugment);

        // Ensure updated augment is no longer in saved
        savedAugments = savedAugments.remove(updatedAugment);
        currentAugments = currentAugments.setAugment(message.position(), updatedAugment);

        // Finally update the stack with the changes
        SavedAugments.set(stack, savedAugments);
        Augments.set(stack, currentAugments);
        menu.setAugments(currentAugments);

        // Sync the change to the client
        Network.getPlay().sendToPlayer(() -> serverPlayer, new MessageSyncAugmentChange(message.position(), updatedAugment));
    }

    public static void handleSetAugmentState(MessageSetAugmentState message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container and must be the wearer
        if(!(serverPlayer.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner())
            return;

        // Only works if in an equipped backpack, not a shelf
        if(!(menu.getBackpackInventory() instanceof BackpackInventory))
            return;

        // Check if valid stack at the given backpack index
        int backpackIndex = menu.getBackpackIndex();
        ItemStack stack = BackpackHelper.getBackpackStack(serverPlayer, backpackIndex);
        if(stack.isEmpty())
            return;

        Augments.set(stack, Augments.get(stack).setState(message.position(), message.state()));
    }

    public static void handleUpdateAugment(MessageUpdateAugment message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container and must be the wearer
        if(!(serverPlayer.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner())
            return;

        // Only works if in an equipped backpack, not a shelf
        if(!(menu.getBackpackInventory() instanceof BackpackInventory))
            return;

        // Check if valid stack at the given backpack index
        int backpackIndex = menu.getBackpackIndex();
        ItemStack stack = BackpackHelper.getBackpackStack(serverPlayer, backpackIndex);
        if(stack.isEmpty())
            return;

        Augments currentAugments = Augments.get(stack);

        // The updating augment must match the augment type of the position it is trying to update
        Augment<?> currentAugment = currentAugments.getAugment(message.position());
        Augment<?> updatedAugment = message.augment();
        if(currentAugment.type() != updatedAugment.type())
            return;

        // Allows augments to perform a sort of sanitization before updating
        updatedAugment = updatedAugment.onUpdate(serverPlayer, currentAugment);

        // Don't need to update if the augments are the same
        if(Objects.equals(currentAugment, updatedAugment))
            return;

        currentAugments = currentAugments.setAugment(message.position(), updatedAugment);
        Augments.set(stack, currentAugments);
        menu.setAugments(currentAugments);

        // Sync change back to client
        Network.getPlay().sendToPlayer(() -> serverPlayer, new MessageSyncAugmentChange(message.position(), updatedAugment));
    }

    public static void handleRenameBackpack(MessageRenameBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container and must be the wearer
        if(!(serverPlayer.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner())
            return;

        // Only works if in an equipped backpack, not a shelf
        if(!(menu.getBackpackInventory() instanceof BackpackInventory))
            return;

        int backpackIndex = menu.getBackpackIndex();
        ItemStack stack = BackpackHelper.getBackpackStack(serverPlayer, backpackIndex);
        if(stack.isEmpty())
            return;

        String value = StringUtil.filterText(message.value());
        if(value.length() <= 50)
        {
            if(StringUtil.isBlank(value))
            {
                stack.remove(DataComponents.CUSTOM_NAME);
            }
            else
            {
                stack.set(DataComponents.CUSTOM_NAME, Component.literal(value));
            }

            // Reopen backpack just to update the name
            BackpackItem.openBackpack(serverPlayer, serverPlayer, backpackIndex);
        }
    }

    public static void handleSortBackpack(MessageSortBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container and must be the wearer
        if(!(serverPlayer.containerMenu instanceof BackpackContainerMenu menu))
            return;

        List<ItemStack> stacks = new ArrayList<>();
        Container container = menu.getBackpackInventory();
        for(int i = 0; i < container.getContainerSize(); i++)
        {
            ItemStack stack = container.getItem(i);
            if(!stack.isEmpty() && container.canTakeItem(container, i, stack) && container.canPlaceItem(i, stack))
            {
                stacks.add(stack);
                container.setItem(i, ItemStack.EMPTY);
            }
        }

        for(int i = 0; i < stacks.size(); i++)
        {
            ItemStack stack = stacks.get(i);
            if(stack.isEmpty() || stack.getCount() >= stack.getMaxStackSize())
                continue;

            for(int j = i + 1; j < stacks.size(); j++)
            {
                ItemStack other = stacks.get(j);
                if(ItemStack.isSameItemSameComponents(stack, other))
                {
                    int grow = Math.min(stack.getMaxStackSize() - stack.getCount(), other.getCount());
                    stack.grow(grow);
                    other.shrink(grow);
                }
            }
        }
        stacks.removeIf(ItemStack::isEmpty);

        // Perform sorting
        ItemSorting sorting = message.sorting();
        if(sorting == ItemSorting.CREATIVE_CATEGORY)
        {
            CreativeCategorySort.buildSortIndex(player.level().registryAccess());
        }
        switch(sorting)
        {
            case SHUFFLE -> stacks.sort(sorting.comparator());
            case ALPHABETICAL -> stacks.sort(sorting.comparator().thenComparing(ItemSorting.MOST_DAMAGED.comparator().reversed()));
            case MOST_DAMAGED -> stacks.sort(sorting.comparator().thenComparing(ItemSorting.ALPHABETICAL.comparator()));
            default -> {
                stacks.sort(sorting.comparator().thenComparing(ItemSorting.ALPHABETICAL.comparator()).thenComparing(ItemSorting.MOST_DAMAGED.comparator().reversed()));
            }
        }

        for(ItemStack stack : stacks)
        {
            for(int i = 0; i < container.getContainerSize(); i++)
            {
                if(container.getItem(i).isEmpty() && container.canPlaceItem(i, stack))
                {
                    container.setItem(i, stack);
                    break;
                }
            }
        }
        container.setChanged();
    }

    public static void handleMessageCheckShelfKey(MessageCheckShelfKey message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container and must be the wearer
        if(!(serverPlayer.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner())
            return;

        // Only works if in an equipped backpack, not a shelf
        if(!(menu.getBackpackInventory() instanceof BackpackInventory))
            return;

        // Backpack index must match current menu's backpack index
        int backpackIndex = menu.getBackpackIndex();
        if(menu.getBackpackIndex() != backpackIndex)
            return;

        if(!menu.getBackpackInventory().stillValid(serverPlayer))
            return;

        ItemStack stack = BackpackHelper.getBackpackStack(serverPlayer, backpackIndex);
        if(stack.isEmpty())
            return;

        Augments.Position position = message.position();
        Augments augments = Augments.get(stack);
        if(!(augments.getAugment(position) instanceof RecallAugment(Optional<ShelfKey> shelfKey)))
            return;

        shelfKey.ifPresentOrElse(key -> {
            boolean valid = false;
            ServerLevel keyLevel = serverPlayer.server.getLevel(key.level());
            if(keyLevel != null) {
                Recall recall = ((Recall.Access) keyLevel).backpacked$getRecall();
                valid = recall.isShelfAtBlockPos(BlockPos.of(key.position()));
            }
            Network.getPlay().sendToPlayer(() -> serverPlayer, new MessageResponseShelfKey(backpackIndex, position, valid));
        }, () -> {
            Network.getPlay().sendToPlayer(() -> serverPlayer, new MessageResponseShelfKey(backpackIndex, position, false));
        });
    }

    public static void handleUnlockAugmentBay(MessageUnlockAugmentBay message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        if(!(player.containerMenu instanceof BackpackContainerMenu menu))
            return;

        if(!menu.stillValid(serverPlayer) || !menu.isOwner())
            return;

        var position = message.position();
        if(menu.getAugmentBayController().isSlotUnlocked(position.ordinal()))
            return;

        if(!menu.getAugmentBayController().handleUnlockSlot(serverPlayer, position.ordinal()))
            return;

        Network.PLAY.sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockAugmentBay(position));
    }
}
