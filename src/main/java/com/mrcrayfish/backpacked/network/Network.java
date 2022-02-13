package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class Network
{
    private static final String PROTOCOL_VERSION = "1";
    private static int nextId = 0;
    private static SimpleChannel playChannel;

    public static void init()
    {
        playChannel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Reference.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        register(MessageOpenBackpack.class, new MessageOpenBackpack(), NetworkDirection.PLAY_TO_SERVER);
        register(MessageUpdateBackpack.class, new MessageUpdateBackpack(), NetworkDirection.PLAY_TO_CLIENT);
        register(MessageEntityBackpack.class, new MessageEntityBackpack(), NetworkDirection.PLAY_TO_SERVER);
        register(MessageBackpackCosmetics.class, new MessageBackpackCosmetics(), NetworkDirection.PLAY_TO_SERVER);
        register(MessageSyncUnlockTracker.class, new MessageSyncUnlockTracker(), NetworkDirection.PLAY_TO_CLIENT);
        register(MessageUnlockBackpack.class, new MessageUnlockBackpack(), NetworkDirection.PLAY_TO_CLIENT);
        register(MessageRequestCustomisation.class, new MessageRequestCustomisation(), NetworkDirection.PLAY_TO_SERVER);
        register(MessageOpenCustomisation.class, new MessageOpenCustomisation(), NetworkDirection.PLAY_TO_CLIENT);
        register(MessageSyncVillagerBackpack.class, new MessageSyncVillagerBackpack(), NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message, NetworkDirection direction)
    {
        playChannel.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle, Optional.of(direction));
    }

    public static SimpleChannel getPlayChannel()
    {
        return playChannel;
    }
}
