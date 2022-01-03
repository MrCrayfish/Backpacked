package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenCustomisation;
import com.mrcrayfish.backpacked.network.message.MessagePlayerBackpack;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

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
            tag.putBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName(), message.isShowWithElytra());
            tag.putBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName(), message.isShowEffects());

            if(Backpacked.isCuriosLoaded())
                return;

            if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
            {
                ItemStack backpackStack = inventory.getBackpackItems().get(0);
                if(!backpackStack.isEmpty() && backpackStack.getItem() instanceof BackpackItem)
                {
                    Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageUpdateBackpack(player.getId(), backpackStack));
                }
            }
        }
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, ServerPlayer player)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.getItem() instanceof BackpackItem)
        {
            ((BackpackItem) backpack.getItem()).showInventory(player);
        }
    }

    public static void handlePlayerBackpack(MessagePlayerBackpack message, ServerPlayer player)
    {
        if(!Config.SERVER.pickpocketBackpacks.get())
            return;

        Entity entity = player.level.getEntity(message.getEntityId());
        if(!(entity instanceof Player))
            return;

        Player otherPlayer = (Player) entity;
        if(!PickpocketUtil.canSeeBackpack(otherPlayer, player))
            return;

        ItemStack backpack = Backpacked.getBackpackStack(otherPlayer);
        if(!backpack.isEmpty())
        {
            BackpackInventory backpackInventory = ((BackpackedInventoryAccess) otherPlayer).getBackpackedInventory();
            if(backpackInventory == null)
                return;
            Component title = backpack.hasCustomHoverName() ? backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
            int rows = ((BackpackItem) backpack.getItem()).getRowCount();
            NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, entity1) -> {
                return new BackpackContainerMenu(id, player.getInventory(), backpackInventory, rows);
            }, title), buffer -> buffer.writeVarInt(rows));
            otherPlayer.displayClientMessage(new TranslatableComponent("message.backpacked.player_opened"), true);
            player.level.playSound(player, otherPlayer.getX(), otherPlayer.getY() + 1.0, otherPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.75F, 1.0F);
        }
    }

    public static void handleRequestCustomisation(MessageRequestCustomisation message, ServerPlayer player)
    {
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
