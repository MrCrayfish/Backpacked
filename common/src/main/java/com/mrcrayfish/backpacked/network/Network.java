package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.network.message.*;
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
        .registerConfigurationMessage("sync_backpacks", MessageSyncBackpacks.class, MessageSyncBackpacks::encode, MessageSyncBackpacks::decode, MessageSyncBackpacks::handle, () -> List.of(BackpackManager.instance().getSyncMessage()))
        .registerPlayMessage("open_backpack", MessageOpenBackpack.class, MessageOpenBackpack::encode, MessageOpenBackpack::decode, MessageOpenBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("update_backpack", MessageUpdateBackpack.class, MessageUpdateBackpack::encode, MessageUpdateBackpack::decode, MessageUpdateBackpack::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("entity_backpack", MessageEntityBackpack.class, MessageEntityBackpack::encode, MessageEntityBackpack::decode, MessageEntityBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("backpack_cosmetics", MessageBackpackCosmetics.class, MessageBackpackCosmetics::encode, MessageBackpackCosmetics::decode, MessageBackpackCosmetics::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("sync_unlock_tracker", MessageSyncUnlockTracker.class, MessageSyncUnlockTracker::encode, MessageSyncUnlockTracker::decode, MessageSyncUnlockTracker::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("unlock_backpack", MessageUnlockBackpack.class, MessageUnlockBackpack::encode, MessageUnlockBackpack::decode, MessageUnlockBackpack::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("request_customisation", MessageRequestCustomisation.class, MessageRequestCustomisation::encode, MessageRequestCustomisation::decode, MessageRequestCustomisation::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("open_customisation", MessageOpenCustomisation.class, MessageOpenCustomisation::encode, MessageOpenCustomisation::decode, MessageOpenCustomisation::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("sync_villager_backpack", MessageSyncVillagerBackpack.class, MessageSyncVillagerBackpack::encode, MessageSyncVillagerBackpack::decode, MessageSyncVillagerBackpack::handle, PacketFlow.CLIENTBOUND)
        .build();

    public static void init() {}

    public static FrameworkNetwork getPlay()
    {
        return PLAY;
    }
}
