package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class Network
{
    public static final FrameworkNetwork PLAY = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "play"), 1)
            .registerHandshakeMessage(MessageSyncBackpacks.class, local -> {
                return List.of(Pair.of("Backpacks", BackpackManager.instance().getSyncMessage()));
            })
            .registerPlayMessage(MessageOpenBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
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
