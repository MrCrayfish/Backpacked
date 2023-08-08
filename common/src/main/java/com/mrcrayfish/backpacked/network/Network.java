package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageEntityBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class Network
{
    public static final FrameworkNetwork PLAY = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "play"), 1)
            .registerPlayMessage(MessageOpenBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageUpdateBackpack.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageEntityBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageBackpackCosmetics.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageSyncUnlockTracker.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageUnlockBackpack.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageRequestCustomisation.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageOpenCustomisation.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageSyncVillagerBackpack.class, MessageDirection.PLAY_CLIENT_BOUND)
            .build();

    public static void init() {}

    public static FrameworkNetwork getPlay()
    {
        return PLAY;
    }
}
