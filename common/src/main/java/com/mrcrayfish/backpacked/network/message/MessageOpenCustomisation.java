package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public record MessageOpenCustomisation(Map<ResourceLocation, Component> progressMap, BackpackProperties properties,
                                       boolean showCosmeticWarning)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenCustomisation> STREAM_CODEC = StreamCodec.of((buf, message) -> {
        buf.writeMap(message.progressMap, FriendlyByteBuf::writeResourceLocation, (buf2, label) -> {
            ComponentSerialization.STREAM_CODEC.encode(buf, label);
        });
        BackpackProperties.STREAM_CODEC.encode(buf, message.properties);
        buf.writeBoolean(message.showCosmeticWarning);
    }, buf -> {
        Map<ResourceLocation, Component> map = buf.readMap(HashMap::new, FriendlyByteBuf::readResourceLocation, buf1 -> {
            return ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf1);
        });
        BackpackProperties properties = BackpackProperties.STREAM_CODEC.decode(buf);
        boolean showCosmeticWarning = buf.readBoolean();
        return new MessageOpenCustomisation(map, properties, showCosmeticWarning);
    });

    public static void handle(MessageOpenCustomisation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleOpenCustomisation(message));
        context.setHandled(true);
    }
}
