package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public record BackpackProperties(Optional<ResourceLocation> cosmetic, boolean showWithElytra, boolean showEffects)
{
    public static final BackpackProperties DEFAULT = new BackpackProperties(Optional.empty(), false, true);

    public static final Codec<BackpackProperties> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ResourceLocation.CODEC.optionalFieldOf("cosmetic").forGetter(o -> o.cosmetic),
        Codec.BOOL.fieldOf("show_with_elytra").forGetter(o -> o.showWithElytra),
        Codec.BOOL.fieldOf("show_effects").forGetter(o -> o.showEffects)
    ).apply(builder, BackpackProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackProperties> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), BackpackProperties::cosmetic,
        ByteBufCodecs.BOOL, BackpackProperties::showWithElytra,
        ByteBufCodecs.BOOL, BackpackProperties::showEffects,
        BackpackProperties::new
    );

    public BackpackProperties setCosmetic(ResourceLocation model)
    {
        return new BackpackProperties(Optional.of(model), this.showWithElytra, this.showEffects);
    }

    public BackpackProperties setShowWithElytra(boolean showWithElytra)
    {
        return new BackpackProperties(this.cosmetic, showWithElytra, this.showEffects);
    }

    public BackpackProperties setShowEffects(boolean showEffects)
    {
        return new BackpackProperties(this.cosmetic, this.showWithElytra, showEffects);
    }
}
