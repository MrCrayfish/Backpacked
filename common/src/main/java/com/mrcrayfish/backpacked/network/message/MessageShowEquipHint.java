package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageShowEquipHint()
{
    public static final MessageShowEquipHint INSTANCE = new MessageShowEquipHint();
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageShowEquipHint> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void handle(MessageShowEquipHint message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageShowEquipHint(message, context));
        context.setHandled(true);
    }
}
