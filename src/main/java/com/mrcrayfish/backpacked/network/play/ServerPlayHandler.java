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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ServerPlayHandler
{
    public static void handleCustomiseBackpack(MessageBackpackCosmetics message, ServerPlayerEntity player)
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

            CompoundNBT tag = stack.getOrCreateTag();
            tag.putString("BackpackModel", id.toString());
            tag.putBoolean(BackpackModelProperty.SHOW_GLINT.getTagName(), message.isShowGlint());
            tag.putBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName(), message.isShowWithElytra());
            tag.putBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName(), message.isShowEffects());

            if(Backpacked.isCuriosLoaded())
                return;

            if(player.inventory instanceof ExtendedPlayerInventory)
            {
                Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageUpdateBackpack(player.getId(), stack));
            }
        }
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, ServerPlayerEntity player)
    {
        BackpackItem.openBackpack(player, player);
    }

    public static void handleEntityBackpack(MessageEntityBackpack message, ServerPlayerEntity player)
    {
        if(!Config.SERVER.pickpocketBackpacks.get())
            return;

        Entity entity = player.level.getEntity(message.getEntityId());
        if(!(entity instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) entity;
        if(!PickpocketUtil.canSeeBackpack(livingEntity, player))
            return;

        //TODO eventually open to all living entities
        if(livingEntity instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity otherPlayer = (ServerPlayerEntity) livingEntity;
            if(BackpackItem.openBackpack(otherPlayer, player))
            {
                otherPlayer.displayClientMessage(new TranslationTextComponent("message.backpacked.player_opened"), true);
                player.level.playSound(player, livingEntity.getX(), livingEntity.getY() + 1.0, livingEntity.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.75F, 1.0F);
            }
        }
        else if(livingEntity instanceof WanderingTraderEntity)
        {
            WanderingTraderEvents.openBackpack((WanderingTraderEntity) livingEntity, player);
        }
    }

    public static void handleRequestCustomisation(MessageRequestCustomisation message, ServerPlayerEntity player)
    {
        if(Backpacked.getBackpackStack(player).isEmpty())
            return;

        UnlockTracker.get(player).ifPresent(unlockTracker ->
        {
            Map<ResourceLocation, ITextComponent> map = new HashMap<>();
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
