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
        .createNetworkBuilder(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "play"), 1)
        .registerConfigurationMessage("sync_backpacks", MessageSyncBackpacks.class, MessageSyncBackpacks.STREAM_CODEC, MessageSyncBackpacks::handle, () -> List.of(BackpackManager.instance().getSyncMessage()))
        .registerPlayMessage("open_backpack", MessageOpenBackpack.class, MessageOpenBackpack.STREAM_CODEC, MessageOpenBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("pickpocket_backpack", MessagePickpocketBackpack.class, MessagePickpocketBackpack.STREAM_CODEC, MessagePickpocketBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("navigate_backpack", MessageNavigateBackpack.class, MessageNavigateBackpack.STREAM_CODEC, MessageNavigateBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("backpack_cosmetics", MessageBackpackCosmetics.class, MessageBackpackCosmetics.STREAM_CODEC, MessageBackpackCosmetics::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("sync_unlock_tracker", MessageSyncUnlockTracker.class, MessageSyncUnlockTracker.STREAM_CODEC, MessageSyncUnlockTracker::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("unlock_backpack", MessageUnlockBackpack.class, MessageUnlockBackpack.STREAM_CODEC, MessageUnlockBackpack::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("request_customisation", MessageRequestCustomisation.class, MessageRequestCustomisation.STREAM_CODEC, MessageRequestCustomisation::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("open_customisation", MessageOpenCustomisation.class, MessageOpenCustomisation.STREAM_CODEC, MessageOpenCustomisation::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("sync_villager_backpack", MessageSyncVillagerBackpack.class, MessageSyncVillagerBackpack.STREAM_CODEC, MessageSyncVillagerBackpack::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("request_management", MessageRequestManagement.class, MessageRequestManagement.STREAM_CODEC, MessageRequestManagement::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("unlock_slot", MessageUnlockSlot.class, MessageUnlockSlot.STREAM_CODEC, MessageUnlockSlot::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("sync_unlock_slot", MessageSyncUnlockSlot.class, MessageSyncUnlockSlot.STREAM_CODEC, MessageSyncUnlockSlot::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("change_augment", MessageChangeAugment.class, MessageChangeAugment.STREAM_CODEC, MessageChangeAugment::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("set_augment_state", MessageSetAugmentState.class, MessageSetAugmentState.STREAM_CODEC, MessageSetAugmentState::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("update_augment", MessageUpdateAugment.class, MessageUpdateAugment.STREAM_CODEC, MessageUpdateAugment::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("lootbound_take_item", MessageLootboundTakeItem.class, MessageLootboundTakeItem.STREAM_CODEC, MessageLootboundTakeItem::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("sync_augment_change", MessageSyncAugmentChange.class, MessageSyncAugmentChange.STREAM_CODEC, MessageSyncAugmentChange::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("rename_backpack", MessageRenameBackpack.class, MessageRenameBackpack.STREAM_CODEC, MessageRenameBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("sort_backpack", MessageSortBackpack.class, MessageSortBackpack.STREAM_CODEC, MessageSortBackpack::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("farmhand_plant", MessageFarmhandPlant.class, MessageFarmhandPlant.STREAM_CODEC, MessageFarmhandPlant::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("shelf_place_animation", MessageShelfPlaceAnimation.class, MessageShelfPlaceAnimation.STREAM_CODEC, MessageShelfPlaceAnimation::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("check_shelf_key", MessageCheckShelfKey.class, MessageCheckShelfKey.STREAM_CODEC, MessageCheckShelfKey::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("response_shelf_key", MessageResponseShelfKey.class, MessageResponseShelfKey.STREAM_CODEC, MessageResponseShelfKey::handle, PacketFlow.CLIENTBOUND)
        .registerPlayMessage("unlock_augment_bat", MessageUnlockAugmentBay.class, MessageUnlockAugmentBay.STREAM_CODEC, MessageUnlockAugmentBay::handle, PacketFlow.SERVERBOUND)
        .registerPlayMessage("sync_unlock_augment_bay", MessageSyncUnlockAugmentBay.class, MessageSyncUnlockAugmentBay.STREAM_CODEC, MessageSyncUnlockAugmentBay::handle, PacketFlow.CLIENTBOUND)
        .build();

    public static void init() {}

    public static FrameworkNetwork getPlay()
    {
        return PLAY;
    }
}
