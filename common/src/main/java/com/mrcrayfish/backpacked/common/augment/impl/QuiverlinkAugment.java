package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public record QuiverlinkAugment(Priority priority) implements Augment<QuiverlinkAugment>
{
    public static final AugmentType<QuiverlinkAugment> TYPE = new AugmentType<>(
        Utils.rl("quiverlink"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Priority.CODEC.fieldOf("priority").orElse(Priority.BACKPACK).forGetter(QuiverlinkAugment::priority)
        ).apply(instance, QuiverlinkAugment::new)),
        StreamCodec.composite(
            Priority.STREAM_CODEC, QuiverlinkAugment::priority,
            QuiverlinkAugment::new
        ),
        () -> new QuiverlinkAugment(Priority.BACKPACK)
    );

    public QuiverlinkAugment setPriority(Priority priority)
    {
        return new QuiverlinkAugment(priority);
    }

    @Override
    public AugmentType<QuiverlinkAugment> type()
    {
        return TYPE;
    }

    public enum Priority implements StringRepresentable, LabelAndDescription
    {
        BACKPACK,
        INVENTORY;

        public static final Codec<Priority> CODEC = StringRepresentable.fromEnum(Priority::values);
        public static final StreamCodec<FriendlyByteBuf, Priority> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Priority.class));
        private static final String LANGUAGE_KEY = "augment.backpacked.quiverlink.priority";

        private final Component name;
        private final Component tooltip;

        Priority()
        {
            this.name = Component.translatable("%s.%s".formatted(LANGUAGE_KEY, this.getSerializedName()));
            this.tooltip = Component.translatable("%s.%s.tooltip".formatted(LANGUAGE_KEY, this.getSerializedName()));
        }

        @Override
        public Component label()
        {
            return this.name;
        }

        @Override
        public Component description()
        {
            return this.tooltip;
        }

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
