package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
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
public record MessageOpenCustomisation(int backpackIndex, Map<ResourceLocation, Component> progressMap, CosmeticProperties properties,
                                       boolean showCosmeticWarning, Map<ResourceLocation, Double> completionProgressMap)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenCustomisation> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, MessageOpenCustomisation::backpackIndex,
        ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ComponentSerialization.STREAM_CODEC), MessageOpenCustomisation::progressMap,
        CosmeticProperties.STREAM_CODEC, MessageOpenCustomisation::properties,
        ByteBufCodecs.BOOL, MessageOpenCustomisation::showCosmeticWarning,
        ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.DOUBLE), MessageOpenCustomisation::completionProgressMap,
        MessageOpenCustomisation::new
    );

    public static void handle(MessageOpenCustomisation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleOpenCustomisation(message));
        context.setHandled(true);
    }
}
