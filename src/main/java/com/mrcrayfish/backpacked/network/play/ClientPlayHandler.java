package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.common.data.UnlockTracker;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

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

        PlayerEntity player = mc.player;
        UnlockTracker.get(player).ifPresent(impl -> {
            impl.unlockBackpack(message.getId());
            //TODO display toast
        });
    }

    public static void handleSyncUnlockTracker(MessageSyncUnlockTracker message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        PlayerEntity player = mc.player;
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
            if(entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;
                if(player.inventory instanceof ExtendedPlayerInventory)
                {
                    ((ExtendedPlayerInventory) player.inventory).getBackpackItems().set(0, message.getBackpack());
                }
            }
        }
    }
}
