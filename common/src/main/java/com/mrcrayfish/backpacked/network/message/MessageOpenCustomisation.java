package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public final class MessageOpenCustomisation extends PlayMessage<MessageOpenCustomisation> // TODO DONE
{
    private int backpackIndex;
    private Map<ResourceLocation, Component> progressMap;
    private CosmeticProperties properties;
    private boolean showCosmeticWarning;
    private Map<ResourceLocation, Double> completionProgressMap;

    public MessageOpenCustomisation() {}

    public MessageOpenCustomisation(int backpackIndex, Map<ResourceLocation, Component> progressMap, CosmeticProperties properties, boolean showCosmeticWarning, Map<ResourceLocation, Double> completionProgressMap)
    {
        this.backpackIndex = backpackIndex;
        this.progressMap = progressMap;
        this.properties = properties;
        this.showCosmeticWarning = showCosmeticWarning;
        this.completionProgressMap = completionProgressMap;
    }

    @Override
    public void encode(MessageOpenCustomisation message, FriendlyByteBuf buf)
    {
        buf.writeInt(message.backpackIndex);
        buf.writeVarInt(message.progressMap.size());
        message.progressMap.forEach((location, formattedProgress) -> {
            buf.writeResourceLocation(location);
            buf.writeComponent(formattedProgress);
        });
        message.properties.encode(buf);
        buf.writeBoolean(message.showCosmeticWarning);
        buf.writeVarInt(message.completionProgressMap.size());
        message.completionProgressMap.forEach((location, value) -> {
            buf.writeResourceLocation(location);
            buf.writeDouble(value);
        });
    }

    @Override
    public MessageOpenCustomisation decode(FriendlyByteBuf buf)
    {
        int backpackIndex = buf.readInt();
        Map<ResourceLocation, Component> progressMap = new HashMap<>();
        int size = buf.readVarInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation id = buf.readResourceLocation();
            Component formattedProgress = buf.readComponent();
            progressMap.put(id, formattedProgress);
        }
        CosmeticProperties properties = CosmeticProperties.decode(buf);
        boolean showCosmeticWarning = buf.readBoolean();
        Map<ResourceLocation, Double> completionProgressMap = new HashMap<>();
        size = buf.readVarInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation id = buf.readResourceLocation();
            double value = buf.readDouble();
            completionProgressMap.put(id, value);
        }
        return new MessageOpenCustomisation(backpackIndex, progressMap, properties, showCosmeticWarning, completionProgressMap);
    }

    @Override
    public void handle(MessageOpenCustomisation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleOpenCustomisation(message));
        context.setHandled(true);
    }

    public int backpackIndex()
    {
        return this.backpackIndex;
    }

    public Map<ResourceLocation, Component> progressMap()
    {
        return this.progressMap;
    }

    public CosmeticProperties properties()
    {
        return this.properties;
    }

    public boolean showCosmeticWarning()
    {
        return this.showCosmeticWarning;
    }

    public Map<ResourceLocation, Double> completionProgressMap()
    {
        return this.completionProgressMap;
    }
}
