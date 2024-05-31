package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.ModelProperty;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageEntityBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.platform.Services;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ServerPlayHandler
{
    public static void handleCustomiseBackpack(MessageBackpackCosmetics message, ServerPlayer player)
    {
        ItemStack stack = Services.BACKPACK.getBackpackStack(player);
        if(!stack.isEmpty())
        {
            ResourceLocation id = message.getBackpackId();
            Backpack backpack = BackpackManager.instance().getBackpack(id);
            if(backpack == null)
                return;

            if(!backpack.isUnlocked(player) && !Config.SERVER.backpack.unlockAllCosmetics.get())
                return;

            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("BackpackModel", id.toString());
            tag.putBoolean(ModelProperty.SHOW_GLINT.getTagName(), message.isShowGlint());
            tag.putBoolean(ModelProperty.SHOW_WITH_ELYTRA.getTagName(), message.isShowWithElytra());
            tag.putBoolean(ModelProperty.SHOW_EFFECTS.getTagName(), message.isShowEffects());
        }
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, ServerPlayer player)
    {
        BackpackItem.openBackpack(player, player);
    }

    public static void handleEntityBackpack(MessageEntityBackpack message, ServerPlayer player)
    {
        Entity entity = player.level().getEntity(message.getEntityId());
        if(!(entity instanceof LivingEntity otherEntity))
            return;

        if(otherEntity instanceof ServerPlayer && !Config.SERVER.pickpocketing.enabled.get())
            return;

        if(!PickpocketUtil.canSeeBackpack(otherEntity, player))
            return;

        //TODO eventually open to all living entities
        if(otherEntity instanceof ServerPlayer otherPlayer)
        {
            if(BackpackItem.openBackpack(otherPlayer, player))
            {
                otherPlayer.displayClientMessage(Component.translatable("message.backpacked.player_opened"), true);
                player.level().playSound(player, otherPlayer.getX(), otherPlayer.getY() + 1.0, otherPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.75F, 1.0F);
            }
        }
        else if(otherEntity instanceof WanderingTrader trader)
        {
            WanderingTraderEvents.openBackpack(trader, player);
        }
    }

    public static void handleRequestCustomisation(MessageRequestCustomisation message, ServerPlayer player)
    {
        if(Config.SERVER.backpack.disableCustomisation.get())
            return;

        if(Services.BACKPACK.getBackpackStack(player).isEmpty())
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
            Network.getPlay().sendToPlayer(() -> player, new MessageOpenCustomisation(map));
        });
    }
}
