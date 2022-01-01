package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageCustomiseBackpack implements IMessage<MessageCustomiseBackpack>
{
    private ResourceLocation id;
    private boolean showWithElytra;
    private boolean showEffects;

    public MessageCustomiseBackpack() {}

    public MessageCustomiseBackpack(ResourceLocation id, boolean showWithElytra, boolean showEffects)
    {
        this.id = id;
        this.showWithElytra = showWithElytra;
        this.showEffects = showEffects;
    }

    @Override
    public void encode(MessageCustomiseBackpack message, PacketBuffer buffer)
    {
        buffer.writeResourceLocation(message.id);
        buffer.writeBoolean(message.showWithElytra);
        buffer.writeBoolean(message.showEffects);
    }

    @Override
    public MessageCustomiseBackpack decode(PacketBuffer buffer)
    {
        return new MessageCustomiseBackpack(buffer.readResourceLocation(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageCustomiseBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ServerPlayHandler.handleCustomiseBackpack(message, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }

    public ResourceLocation getBackpackId()
    {
        return this.id;
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
