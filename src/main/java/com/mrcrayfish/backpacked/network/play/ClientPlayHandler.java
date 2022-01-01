package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.common.data.UnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

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
}
