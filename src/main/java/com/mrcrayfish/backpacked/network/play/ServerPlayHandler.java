package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageEntityBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ServerPlayHandler
{
    public static void handleCustomiseBackpack(MessageBackpackCosmetics message, ServerPlayer player)
    {
        ItemStack stack = Backpacked.getBackpackStack(player);
        if(!stack.isEmpty())
        {
            ResourceLocation id = message.getBackpackId();
            Backpack backpack = BackpackManager.instance().getBackpack(id);
            if(backpack == null)
                return;

            if(!backpack.isUnlocked(player) && !Config.SERVER.unlockAllBackpacks.get())
                return;

            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("BackpackModel", id.toString());
            tag.putBoolean(BackpackModelProperty.SHOW_GLINT.getTagName(), message.isShowGlint());
            tag.putBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName(), message.isShowWithElytra());
            tag.putBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName(), message.isShowEffects());

            if(Backpacked.isCuriosLoaded())
                return;

            if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
            {
                Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageUpdateBackpack(player.getId(), stack));
                Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateBackpack(player.getId(), stack, true));
            }
        }
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, ServerPlayer player)
    {
        BackpackItem.openBackpack(player, player);
    }

    public static void handleEntityBackpack(MessageEntityBackpack message, ServerPlayer player)
    {
        Entity entity = player.level.getEntity(message.getEntityId());
        if(!(entity instanceof LivingEntity otherEntity))
            return;

        if(otherEntity instanceof ServerPlayer && !Config.SERVER.pickpocketBackpacks.get())
            return;

        if(!PickpocketUtil.canSeeBackpack(otherEntity, player))
            return;

        //TODO eventually open to all living entities
        if(otherEntity instanceof ServerPlayer otherPlayer)
        {
            if(BackpackItem.openBackpack(otherPlayer, player))
            {
                otherPlayer.displayClientMessage(Component.translatable("message.backpacked.player_opened"), true);
                player.level.playSound(player, otherPlayer.getX(), otherPlayer.getY() + 1.0, otherPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.75F, 1.0F);
            }
        }
        else if(otherEntity instanceof WanderingTrader trader)
        {
            WanderingTraderEvents.openBackpack(trader, player);
        }
    }

    public static void handleRequestCustomisation(MessageRequestCustomisation message, ServerPlayer player)
    {
        if(Config.SERVER.disableCustomisation.get())
            return;

        if(Backpacked.getBackpackStack(player).isEmpty())
            return;

        UnlockTracker.get(player).ifPresent(unlockTracker ->
        {
            Map<ResourceLocation, Component> map = new HashMap<>();
            for(Backpack backpack : BackpackManager.instance().getRegisteredBackpacks())
            {
                if(!unlockTracker.isUnlocked(backpack.getId()))
                {
                    unlockTracker.getProgressTracker(backpack.getId()).ifPresent(progressTracker ->
                    {
                        map.put(backpack.getId(), progressTracker.getDisplayComponent());
                    });
                }
            }
            Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> player), new MessageOpenCustomisation(map));
        });
    }
}
