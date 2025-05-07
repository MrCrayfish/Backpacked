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
public record BackpackProperties(Optional<ResourceLocation> cosmetic, boolean showWithElytra, boolean showEffects, boolean showEnchantmentGlint)
{
    public static final BackpackProperties DEFAULT = new BackpackProperties(Optional.empty(), false, true, false);

    public static final Codec<BackpackProperties> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ResourceLocation.CODEC.optionalFieldOf("cosmetic").forGetter(o -> o.cosmetic),
        Codec.BOOL.fieldOf("show_with_elytra").forGetter(o -> o.showWithElytra),
        Codec.BOOL.fieldOf("show_effects").forGetter(o -> o.showEffects),
        Codec.BOOL.fieldOf("show_enchantment_glint").forGetter(o -> o.showEnchantmentGlint)
    ).apply(builder, BackpackProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackProperties> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), BackpackProperties::cosmetic,
        ByteBufCodecs.BOOL, BackpackProperties::showWithElytra,
        ByteBufCodecs.BOOL, BackpackProperties::showEffects,
        ByteBufCodecs.BOOL, BackpackProperties::showEnchantmentGlint,
        BackpackProperties::new
    );

    public BackpackProperties setCosmetic(ResourceLocation model)
    {
        return new BackpackProperties(Optional.of(model), this.showWithElytra, this.showEffects, this.showEnchantmentGlint);
    }

    public BackpackProperties setShowWithElytra(boolean showWithElytra)
    {
        return new BackpackProperties(this.cosmetic, showWithElytra, this.showEffects, this.showEnchantmentGlint);
    }

    public BackpackProperties setShowEffects(boolean showEffects)
    {
        return new BackpackProperties(this.cosmetic, this.showWithElytra, showEffects, this.showEnchantmentGlint);
    }

    public BackpackProperties setShowEnchantmentGlint(boolean showEnchantmentGlint)
    {
        return new BackpackProperties(this.cosmetic, this.showWithElytra, this.showEffects, showEnchantmentGlint);
    }
}
