package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
 // TODO DONE
public record LightweaverAugment(int minimumLight, boolean sound) implements Augment<LightweaverAugment>
{
    public static final AugmentType<LightweaverAugment> TYPE = new AugmentType<>(
        Utils.rl("lightweaver"),
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("minimum_light").orElse(6).forGetter(LightweaverAugment::minimumLight),
            Codec.BOOL.fieldOf("sound").orElse(true).forGetter(LightweaverAugment::sound)
        ).apply(instance, LightweaverAugment::new)),
        LightweaverAugment::encode,
        LightweaverAugment::decode,
        () -> new LightweaverAugment(6, true)
    );

    public LightweaverAugment(int minimumLight, boolean sound)
    {
        this.minimumLight = Mth.clamp(minimumLight, 0, 15);
        this.sound = sound;
    }

    @Override
    public AugmentType<LightweaverAugment> type()
    {
        return TYPE;
    }

    public LightweaverAugment setMinimumLight(int minimumLight)
    {
        return new LightweaverAugment(minimumLight, this.sound);
    }

    public LightweaverAugment setSound(boolean sound)
    {
        return new LightweaverAugment(this.minimumLight, sound);
    }

    private static void encode(FriendlyByteBuf buf, LightweaverAugment augment)
    {
        buf.writeInt(augment.minimumLight);
        buf.writeBoolean(augment.sound);
    }

    private static LightweaverAugment decode(FriendlyByteBuf buf)
    {
        int minimumLight = buf.readInt();
        boolean sound = buf.readBoolean();
        return new LightweaverAugment(minimumLight, sound);
    }
}
