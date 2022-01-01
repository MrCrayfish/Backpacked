package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageCustomiseBackpack implements IMessage<MessageCustomiseBackpack>
{
    private String model;
    private boolean showWithElytra;
    private boolean showEffects;

    public MessageCustomiseBackpack() {}

    public MessageCustomiseBackpack(String model, boolean showWithElytra, boolean showEffects)
    {
        this.model = model;
        this.showWithElytra = showWithElytra;
        this.showEffects = showEffects;
    }

    @Override
    public void encode(MessageCustomiseBackpack message, PacketBuffer buffer)
    {
        buffer.writeUtf(message.model);
        buffer.writeBoolean(message.showWithElytra);
        buffer.writeBoolean(message.showEffects);
    }

    @Override
    public MessageCustomiseBackpack decode(PacketBuffer buffer)
    {
        return new MessageCustomiseBackpack(buffer.readUtf(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageCustomiseBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ServerPlayHandler.handleCustomiseBackpack(message, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }

    public String getModel()
    {
        return this.model;
    }

    public boolean isShowWithElytra()
    {
        return this.showWithElytra;
    }

    public boolean isShowEffects()
    {
        return this.showEffects;
    }
}
