package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.toasts.UnlockBackpackToast;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.network.message.MessageOpenCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;

/**
 * Author: MrCrayfish
 */
public class ClientPlayHandler
{
    public static void handleUnlockBackpack(MessageUnlockBackpack message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        Player player = mc.player;
        UnlockTracker.get(player).ifPresent(impl ->
        {
            Backpack backpack = BackpackManager.instance().getBackpack(message.getId());
            if(backpack != null)
            {
                impl.unlockBackpack(message.getId());
                mc.getToasts().addToast(new UnlockBackpackToast(backpack));
            }
        });
    }

    public static void handleSyncUnlockTracker(MessageSyncUnlockTracker message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        Player player = mc.player;
        UnlockTracker.get(player).ifPresent(impl -> {
            message.getUnlockedBackpacks().forEach(impl::unlockBackpack);
        });
    }

    public static void handleUpdateBackpack(MessageUpdateBackpack message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level != null)
        {
            Entity entity = minecraft.level.getEntity(message.getEntityId());
            if(entity instanceof Player player)
            {
                if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
                {
                    inventory.getBackpackItems().set(0, message.getBackpack());
                }
            }
        }
    }

    public static void handleOpenCustomisation(MessageOpenCustomisation message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null)
            return;

        minecraft.setScreen(new CustomiseBackpackScreen(message.getProgressMap()));
    }

    public static void handleSyncVillagerBackpack(MessageSyncVillagerBackpack message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;

        Entity entity = minecraft.level.getEntity(message.getEntityId());
        if(entity instanceof WanderingTrader trader)
        {
            PickpocketChallenge.get(trader).ifPresent(data -> data.setBackpackEquipped(true));
        }
    }
}
