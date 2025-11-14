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
public record CosmeticProperties(Optional<ResourceLocation> cosmetic, boolean showWithElytra, boolean showEffects)
{
    public static final CosmeticProperties DEFAULT = new CosmeticProperties(Optional.empty(), false, true);

    public static final Codec<CosmeticProperties> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ResourceLocation.CODEC.optionalFieldOf("cosmetic").forGetter(o -> o.cosmetic),
        Codec.BOOL.fieldOf("show_with_elytra").forGetter(o -> o.showWithElytra),
        Codec.BOOL.fieldOf("show_effects").forGetter(o -> o.showEffects)
    ).apply(builder, CosmeticProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CosmeticProperties> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), CosmeticProperties::cosmetic,
        ByteBufCodecs.BOOL, CosmeticProperties::showWithElytra,
        ByteBufCodecs.BOOL, CosmeticProperties::showEffects,
        CosmeticProperties::new
    );

    public CosmeticProperties setCosmetic(ResourceLocation model)
    {
        return new CosmeticProperties(Optional.of(model), this.showWithElytra, this.showEffects);
    }

    public CosmeticProperties setShowWithElytra(boolean showWithElytra)
    {
        return new CosmeticProperties(this.cosmetic, showWithElytra, this.showEffects);
    }

    public CosmeticProperties setShowEffects(boolean showEffects)
    {
        return new CosmeticProperties(this.cosmetic, this.showWithElytra, showEffects);
    }
}
