package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenCustomisation implements IMessage<MessageOpenCustomisation>
{
    private Map<ResourceLocation, ITextComponent> map;

    public MessageOpenCustomisation() {}

    public MessageOpenCustomisation(Map<ResourceLocation, ITextComponent> map)
    {
        this.map = map;
    }

    @Override
    public void encode(MessageOpenCustomisation message, PacketBuffer buffer)
    {
        buffer.writeInt(message.map.size());
        message.map.forEach((location, formattedProgress) -> {
            buffer.writeResourceLocation(location);
            buffer.writeComponent(formattedProgress);
        });
    }

    @Override
    public MessageOpenCustomisation decode(PacketBuffer buffer)
    {
        Map<ResourceLocation, ITextComponent> map = new HashMap<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation id = buffer.readResourceLocation();
            ITextComponent formattedProgress = buffer.readComponent();
            map.put(id, formattedProgress);
        }
        return new MessageOpenCustomisation(map);
    }

    @Override
    public void handle(MessageOpenCustomisation message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleOpenCustomisation(message));
        supplier.get().setPacketHandled(true);
    }

    public Map<ResourceLocation, ITextComponent> getProgressMap()
    {
        return this.map;
    }
}
