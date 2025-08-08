package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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

        BackpackProperties properties = message.properties();
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

        stack.set(ModDataComponents.BACKPACK_PROPERTIES.get(), properties);
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(player instanceof ServerPlayer serverPlayer)
        {
            BackpackItem.openBackpack(serverPlayer, serverPlayer);
        }
    }

    public static void handleEntityBackpack(MessageEntityBackpack message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(player == null)
            return;

        Entity entity = player.level().getEntity(message.entityId());
        if(!(entity instanceof LivingEntity otherEntity))
            return;

        if(otherEntity instanceof ServerPlayer && !Config.PICKPOCKETING.enabled.get())
            return;

        if(!PickpocketUtil.canSeeBackpack(otherEntity, player))
            return;

        //TODO eventually open to all living entities
        if(otherEntity instanceof ServerPlayer otherPlayer)
        {
            if(BackpackItem.openBackpack(otherPlayer, (ServerPlayer) player))
            {
                otherPlayer.displayClientMessage(Component.translatable("message.backpacked.player_opened"), true);
                player.level().playSound(player, otherPlayer.getX(), otherPlayer.getY() + 1.0, otherPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 0.75F, 1.0F);
            }
        }
        else if(otherEntity instanceof WanderingTrader trader)
        {
            WanderingTraderEvents.openBackpack(trader, (ServerPlayer) player);
        }
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
        Map<ResourceLocation, Component> map = new HashMap<>();
        UnlockManager.getTracker(player).ifPresent(unlockTracker -> {
            for(Backpack backpack : BackpackManager.instance().getBackpacks()) {
                if(!unlockTracker.isUnlocked(backpack.getId())) {
                    unlockTracker.getProgressTracker(backpack.getId()).ifPresent(progressTracker -> {
                        map.put(backpack.getId(), progressTracker.getDisplayComponent());
                    });
                }
            }
        });
        BackpackProperties properties = stack.getOrDefault(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT);
        serverPlayer.closeContainer();
        Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageOpenCustomisation(message.backpackIndex(), map, properties, showCosmeticWarning));
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
        context.getPlayer().ifPresent(player -> {
            AbstractContainerMenu menu = player.containerMenu;
            if(menu == null)
                return;
            int slotIndex = message.slotIndex();
            if(slotIndex >= 0 && slotIndex < menu.slots.size()) {
                Slot slot = menu.getSlot(slotIndex);
                if(slot instanceof UnlockableSlot unlockableSlot) {
                    unlockableSlot.unlock((ServerPlayer) player);
                }
            }
        });
    }

    public static void handleNavigateBackpackIndex(MessageNavigateBackpackIndex message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Player must be in a backpack container
        if(!(player.containerMenu instanceof BackpackContainerMenu menu) || !menu.isOwner())
            return;

        // Only works if in an equipped backpack, not a shelf
        if(!(menu.getBackpackInventory() instanceof BackpackInventory))
            return;

        int selected = BackpackHelper.getSelectedBackpackIndex(player);
        int newSelected = BackpackHelper.navigateSelectedBackpackIndex(player, message.forward() ? 1 : -1);
        if(selected != newSelected)
        {
            BackpackItem.openBackpack(serverPlayer, serverPlayer);
        }
    }
}
