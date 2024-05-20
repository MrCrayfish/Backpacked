package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public abstract class Challenge
{
    public static final Codec<Challenge> CODEC = ChallengeSerializer.CODEC.dispatch(Challenge::getSerializer, ChallengeSerializer::codec);

    private final ResourceLocation id;

    protected Challenge(ResourceLocation id)
    {
        this.id = id;
    }

    public abstract ChallengeSerializer<?> getSerializer();

    public abstract IProgressTracker createProgressTracker();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public final void write(FriendlyByteBuf buf)
    {
        buf.writeResourceLocation(this.id);
        ((ChallengeSerializer) this.getSerializer()).write(this, buf);
    }

    public static Challenge read(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        ChallengeSerializer<?> serializer = ChallengeManager.instance().getSerializer(id);
        if(serializer == null) throw new RuntimeException("No challenge serializer registered for the id: " + id);
        return serializer.read(buf);
    }
}
