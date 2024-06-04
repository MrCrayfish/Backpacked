package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageEntityBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageSyncBackpacks;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class Network
{
    public static final FrameworkNetwork PLAY = FrameworkAPI
        .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "play"), 1)
        .registerConfigurationMessage("sync_backpacks", MessageSyncBackpacks.class, MessageSyncBackpacks.STREAM_CODEC, MessageSyncBackpacks::handle, () -> List.of(BackpackManager.instance().getSyncMessage()))
        .registerPlayMessage("open_backpack", MessageOpenBackpack.class, MessageOpenBackpack.STREAM_CODEC, MessageOpenBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("entity_backpack", MessageEntityBackpack.class, MessageEntityBackpack.STREAM_CODEC, MessageEntityBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("backpack_cosmetics", MessageBackpackCosmetics.class, MessageBackpackCosmetics.STREAM_CODEC, MessageBackpackCosmetics::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("sync_unlock_tracker", MessageSyncUnlockTracker.class, MessageSyncUnlockTracker.STREAM_CODEC, MessageSyncUnlockTracker::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("unlock_backpack", MessageUnlockBackpack.class, MessageUnlockBackpack.STREAM_CODEC, MessageUnlockBackpack::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("request_customisation", MessageRequestCustomisation.class, MessageRequestCustomisation.STREAM_CODEC, MessageRequestCustomisation::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("open_customisation", MessageOpenCustomisation.class, MessageOpenCustomisation.STREAM_CODEC, MessageOpenCustomisation::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("sync_villager_backpack", MessageSyncVillagerBackpack.class, MessageSyncVillagerBackpack.STREAM_CODEC, MessageSyncVillagerBackpack::handle, PacketFlow.CLIENTBOUND)
        .build();

    public static void init() {}

    public static FrameworkNetwork getPlay()
    {
        return PLAY;
    }
}
