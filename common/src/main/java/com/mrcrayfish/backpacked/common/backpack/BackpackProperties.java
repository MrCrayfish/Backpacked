package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record BackpackProperties(ResourceLocation model, boolean showWithElytra, boolean showEffects, boolean showEnchantmentGlint)
{
    public static final BackpackProperties DEFAULT = new BackpackProperties(Backpack.DEFAULT_MODEL, false, true, false);

    public static final Codec<BackpackProperties> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ResourceLocation.CODEC.fieldOf("model").forGetter(o -> o.model),
        Codec.BOOL.fieldOf("show_with_elytra").forGetter(o -> o.showWithElytra),
        Codec.BOOL.fieldOf("show_effects").forGetter(o -> o.showEffects),
        Codec.BOOL.fieldOf("show_enchantment_glint").forGetter(o -> o.showEnchantmentGlint)
    ).apply(builder, BackpackProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackProperties> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, BackpackProperties::model,
        ByteBufCodecs.BOOL, BackpackProperties::showWithElytra,
        ByteBufCodecs.BOOL, BackpackProperties::showEffects,
        ByteBufCodecs.BOOL, BackpackProperties::showEnchantmentGlint,
        BackpackProperties::new
    );

    public BackpackProperties setModel(ResourceLocation model)
    {
        return new BackpackProperties(model, this.showWithElytra, this.showEffects, this.showEnchantmentGlint);
    }

    public BackpackProperties setShowWithElytra(boolean showWithElytra)
    {
        return new BackpackProperties(this.model, showWithElytra, this.showEffects, this.showEnchantmentGlint);
    }

    public BackpackProperties setShowEffects(boolean showEffects)
    {
        return new BackpackProperties(this.model, this.showWithElytra, showEffects, this.showEnchantmentGlint);
    }

    public BackpackProperties setShowEnchantmentGlint(boolean showEnchantmentGlint)
    {
        return new BackpackProperties(this.model, this.showWithElytra, this.showEffects, showEnchantmentGlint);
    }
}
