package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenBackpack implements IMessage<MessageOpenBackpack>
{
    @Override
    public void encode(MessageOpenBackpack message, FriendlyByteBuf buffer) {}

    @Override
    public MessageOpenBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageOpenBackpack();
    }

    @Override
    public void handle(MessageOpenBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ServerPlayHandler.handleOpenBackpack(message, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }
}
