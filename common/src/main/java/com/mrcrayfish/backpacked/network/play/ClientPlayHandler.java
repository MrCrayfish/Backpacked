package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.toasts.UnlockBackpackToast;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.data.pickpocket.PickpocketChallenge;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
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
        UnlockManager.getTracker(player).ifPresent(impl ->
        {
            Backpack backpack = BackpackManager.instance().getClientBackpack(message.cosmeticId());
            if(backpack != null)
            {
                impl.unlockBackpack(message.cosmeticId());
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
        UnlockManager.getTracker(player).ifPresent(impl -> {
            message.unlockedBackpacks().forEach(impl::unlockBackpack);
        });
    }

    public static void handleUpdateBackpack(MessageUpdateBackpack message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level != null)
        {
            Entity entity = minecraft.level.getEntity(message.entityId());
            if(entity instanceof Player player)
            {
                if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
                {
                    inventory.getBackpackItems().set(0, message.backpack());
                }
            }
        }
    }

    public static void handleOpenCustomisation(MessageOpenCustomisation message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null)
            return;

        minecraft.setScreen(new CustomiseBackpackScreen(message.progressMap()));
    }

    public static void handleSyncVillagerBackpack(MessageSyncVillagerBackpack message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;

        Entity entity = minecraft.level.getEntity(message.entityId());
        if(entity instanceof WanderingTrader trader)
        {
            PickpocketChallenge.get(trader).ifPresent(data -> data.setBackpackEquipped(true));
        }
    }
}
