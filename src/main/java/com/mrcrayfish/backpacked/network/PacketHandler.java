package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.network.message.IMessage;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Author: MrCrayfish
 */
public class PacketHandler
{
    private static final String PROTOCOL_VERSION = "1";
    private static int nextId = 0;
    public static SimpleChannel instance;

    public static void init()
    {
        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Reference.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        register(MessageOpenBackpack.class, new MessageOpenBackpack());
        register(MessageUpdateBackpack.class, new MessageUpdateBackpack());
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message)
    {
        instance.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }
}
