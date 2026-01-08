package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public record MessageSyncUnlockTracker(Set<Identifier> unlockedBackpacks)
{
    public static final StreamCodec<ByteBuf, Set<Identifier>> RESOURCE_LOCATION_SET_STREAM_CODEC = Identifier.STREAM_CODEC.apply(
        ByteBufCodecs.collection(HashSet::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncUnlockTracker> STREAM_CODEC = StreamCodec.composite(
        RESOURCE_LOCATION_SET_STREAM_CODEC, MessageSyncUnlockTracker::unlockedBackpacks,
        MessageSyncUnlockTracker::new
    );

    public static void handle(MessageSyncUnlockTracker message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncUnlockTracker(message));
        context.setHandled(true);
    }
}
