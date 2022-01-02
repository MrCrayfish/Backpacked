package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageRequestCustomisation implements IMessage<MessageRequestCustomisation>
{
    @Override
    public void encode(MessageRequestCustomisation message, PacketBuffer buffer) {}

    @Override
    public MessageRequestCustomisation decode(PacketBuffer buffer)
    {
        return new MessageRequestCustomisation();
    }

    @Override
    public void handle(MessageRequestCustomisation message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ServerPlayHandler.handleRequestCustomisation(message, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }
}
