package com.mrcrayfish.backpacked.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum Navigate
{
    CURRENT(0), PREVIOUS(-1), NEXT(1);

    public static final StreamCodec<RegistryFriendlyByteBuf, Navigate> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Navigate.class));

    private final int step;

    Navigate(int step)
    {
        this.step = step;
    }

    public int step()
    {
        return this.step;
    }
}
