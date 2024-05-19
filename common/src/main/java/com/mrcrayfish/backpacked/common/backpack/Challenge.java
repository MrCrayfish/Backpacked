package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public class Challenge
{
    public static final Challenge EMPTY = new Challenge("empty");
    public static final Codec<Challenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.STRING.fieldOf("type").forGetter(challenge -> {
            return challenge.type;
        })).apply(builder, Challenge::new);
    });

    private final String type;

    public Challenge(String type)
    {
        this.type = type;
    }

    public Challenge(FriendlyByteBuf buf)
    {
        this.type = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf)
    {
        buf.writeUtf(this.type);
    }
}
