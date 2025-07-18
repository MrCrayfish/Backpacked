package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
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

        ItemStack stack = BackpackHelper.getBackpackStack(player);
        if(!stack.isEmpty())
        {
            BackpackProperties properties = message.properties();
            Optional<ResourceLocation> cosmeticOptional = properties.cosmetic();
            if(cosmeticOptional.isPresent())
            {
                ResourceLocation cosmetic = cosmeticOptional.get();
                Backpack backpack = BackpackManager.instance().getBackpack(cosmetic);
                if(backpack == null)
                    return;

                if(!backpack.isUnlocked(player) && !Config.SERVER.backpack.unlockAllCosmetics.get())
                    return;
            }
            stack.set(ModDataComponents.BACKPACK_PROPERTIES.get(), properties);
        }
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

        if(otherEntity instanceof ServerPlayer && !Config.SERVER.pickpocketing.enabled.get())
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

        if(Config.SERVER.backpack.disableCustomisation.get())
            return;

        if(BackpackHelper.getBackpackStack(player).isEmpty())
            return;

        UnlockManager.getTracker(player).ifPresent(unlockTracker ->
        {
            Map<ResourceLocation, Component> map = new HashMap<>();
            for(Backpack backpack : BackpackManager.instance().getBackpacks())
            {
                if(!unlockTracker.isUnlocked(backpack.getId()))
                {
                    unlockTracker.getProgressTracker(backpack.getId()).ifPresent(progressTracker ->
                    {
                        map.put(backpack.getId(), progressTracker.getDisplayComponent());
                    });
                }
            }
            serverPlayer.closeContainer();
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageOpenCustomisation(map));
        });
    }

    public static void handleRequestManagement(MessageRequestManagement message, MessageContext context)
    {
        context.getPlayer().filter(player -> player instanceof ServerPlayer).ifPresent(player -> {
            BackpackItem.openBackpackManagement((ServerPlayer) player);
        });
    }

    public static void handleUnlockSlot(MessageUnlockSlot message, MessageContext context)
    {
        Player player = context.getPlayer().orElse(null);
        if(!(player instanceof ServerPlayer serverPlayer))
            return;

        // Don't allow unlocking slots unless the wearer
        if(!(player.containerMenu instanceof BackpackContainerMenu menu))
            return;

        ItemStack backpack = BackpackHelper.getBackpackStack(player);
        if(menu.getBackpackInventory() instanceof ShelfBlockEntity.ShelfContainer container)
        {
            backpack = container.getBackpack();
        }

        if(backpack.isEmpty())
            return;

        UnlockedSlots slots = backpack.get(ModDataComponents.UNLOCKED_SLOTS.get());
        if(slots == null || !slots.isUnlockable(message.slot()))
            return;

        // Ensure the player has the experience levels
        int experienceLevelCost = slots.nextUnlockCost();
        if(!player.isCreative() && player.experienceLevel < experienceLevelCost)
            return;

        // Take the experience levels from the player
        player.giveExperienceLevels(-experienceLevelCost);
        menu.unlockSlot(message.slot());

        // Finally unlock the slot and sync the changes to the client
        slots = slots.unlockSlot(message.slot());
        backpack.set(ModDataComponents.UNLOCKED_SLOTS.get(), slots);

        // Ensure shelf saves the changes
        menu.getBackpackInventory().setChanged();

        // Sync to players that are currently in the same menu
        List<ServerPlayer> players = serverPlayer.server.getPlayerList().getPlayers();
        players.stream().filter(otherPlayer -> {
            if(otherPlayer.containerMenu instanceof BackpackContainerMenu otherMenu) {
                return menu.getBackpackInventory() == otherMenu.getBackpackInventory();
            }
            return false;
        }).forEach(otherPlayer -> {
            Network.PLAY.sendToPlayer(() -> otherPlayer, new MessageSyncUnlockSlot(message.slot()));
        });
    }
}
