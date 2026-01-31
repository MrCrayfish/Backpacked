package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.network.protocol.PacketFlow;
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
            .registerPlayMessage(MessagePickpocketBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageNavigateBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageBackpackCosmetics.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageSyncUnlockTracker.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageUnlockBackpack.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageRequestCustomisation.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageOpenCustomisation.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageSyncVillagerBackpack.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageRequestManagement.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageUnlockSlot.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageSyncUnlockSlot.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageChangeAugment.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageSetAugmentState.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageUpdateAugment.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageLootboundTakeItem.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageSyncAugmentChange.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageRenameBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageSortBackpack.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageFarmhandPlant.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageShelfPlaceAnimation.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageCheckShelfKey.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageResponseShelfKey.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageUnlockAugmentBay.class, MessageDirection.PLAY_SERVER_BOUND)
            .registerPlayMessage(MessageSyncUnlockAugmentBay.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(MessageShowEquipHint.class, MessageDirection.PLAY_CLIENT_BOUND)
            .build();

    public static void init() {}

    public static FrameworkNetwork getPlay()
    {
        return PLAY;
    }
}
