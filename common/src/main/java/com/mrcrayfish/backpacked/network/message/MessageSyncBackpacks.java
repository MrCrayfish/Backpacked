package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.network.configuration.ClientConfigurationHandler;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record MessageSyncBackpacks(List<Backpack> backpacks)
{
    public static final StreamCodec<FriendlyByteBuf, MessageSyncBackpacks> STREAM_CODEC = StreamCodec.composite(
        Backpack.LIST_STREAM_CODEC, MessageSyncBackpacks::backpacks,
        MessageSyncBackpacks::new
    );

    public static FrameworkResponse handle(MessageSyncBackpacks message, Consumer<Runnable> executor)
    {
        return ClientConfigurationHandler.handleMessageSyncBackpacks(message, executor);
    }
}
