package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.network.message.IMessage;
import com.mrcrayfish.backpacked.network.message.MessageCustomiseBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessagePlayerBackpack;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class Network
{
    private static final String PROTOCOL_VERSION = "1";
    private static int nextId = 0;
    private static SimpleChannel playChannel;

    //TODO fix direction
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
        register(MessagePlayerBackpack.class, new MessagePlayerBackpack(), NetworkDirection.PLAY_TO_CLIENT);
        register(MessageCustomiseBackpack.class, new MessageCustomiseBackpack(), NetworkDirection.PLAY_TO_SERVER);
        register(MessageSyncUnlockTracker.class, new MessageSyncUnlockTracker(), NetworkDirection.PLAY_TO_CLIENT);
        register(MessageUnlockBackpack.class, new MessageUnlockBackpack(), NetworkDirection.PLAY_TO_CLIENT);
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
