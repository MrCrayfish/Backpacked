package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public final class MessageBackpackCosmetics extends PlayMessage<MessageBackpackCosmetics> // TODO DONE
{
    private int backpackIndex;
    private CosmeticProperties properties;

    public MessageBackpackCosmetics() {}

    public MessageBackpackCosmetics(int backpackIndex, CosmeticProperties properties)
    {
        this.backpackIndex = backpackIndex;
        this.properties = properties;
    }

    @Override
    public void encode(MessageBackpackCosmetics message, FriendlyByteBuf buf)
    {
        buf.writeVarInt(message.backpackIndex);
        message.properties.encode(buf);
    }

    @Override
    public MessageBackpackCosmetics decode(FriendlyByteBuf buf)
    {
        int backpackIndex = buf.readVarInt();
        CosmeticProperties properties = CosmeticProperties.decode(buf);
        return new MessageBackpackCosmetics(backpackIndex, properties);
    }

    @Override
    public void handle(MessageBackpackCosmetics message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleCustomiseBackpack(message, context));
        context.setHandled(true);
    }

    public int backpackIndex()
    {
        return this.backpackIndex;
    }

    public CosmeticProperties properties()
    {
        return this.properties;
    }
}
