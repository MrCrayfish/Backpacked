package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CosmeticProperties
{
    public static final CosmeticProperties DEFAULT = new CosmeticProperties(Optional.empty(), false, true);

    public static final Codec<CosmeticProperties> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.optionalFieldOf("cosmetic").forGetter(o -> o.cosmetic),
            Codec.BOOL.fieldOf("show_with_elytra").forGetter(o -> o.showWithElytra),
            Codec.BOOL.fieldOf("show_effects").forGetter(o -> o.showEffects)
    ).apply(builder, CosmeticProperties::new));

    private Optional<String> cosmetic;
    private boolean showWithElytra;
    private boolean showEffects;

    public CosmeticProperties(Optional<String> cosmetic, boolean showWithElytra, boolean showEffects)
    {
        this.cosmetic = cosmetic;
        this.showWithElytra = showWithElytra;
        this.showEffects = showEffects;
    }

    private CosmeticProperties() {}

    public Optional<String> cosmetic()
    {
        return this.cosmetic;
    }

    public void setCosmetic(String cosmeticId)
    {
        this.cosmetic = Optional.of(cosmeticId);
    }

    public boolean showWithElytra()
    {
        return this.showWithElytra;
    }

    public void setShowWithElytra(boolean showWithElytra)
    {
        this.showWithElytra = showWithElytra;
    }

    public boolean showEffects()
    {
        return this.showEffects;
    }

    public void setShowEffects(boolean showEffects)
    {
        this.showEffects = showEffects;
    }

    public CosmeticProperties copy()
    {
        return new CosmeticProperties(this.cosmetic(), this.showWithElytra(), this.showEffects());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeOptional(this.cosmetic(), FriendlyByteBuf::writeUtf);
        buf.writeBoolean(this.showWithElytra());
        buf.writeBoolean(this.showEffects());
    }

    public static CosmeticProperties decode(FriendlyByteBuf buf)
    {
        Optional<String> cosmetic = buf.readOptional(FriendlyByteBuf::readUtf);
        boolean showWithElytra = buf.readBoolean();
        boolean showEffects = buf.readBoolean();
        return new CosmeticProperties(cosmetic, showWithElytra, showEffects);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CosmeticProperties) obj;
        return Objects.equals(this.cosmetic(), that.cosmetic()) &&
                this.showWithElytra() == that.showWithElytra() &&
                this.showEffects() == that.showEffects();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.cosmetic(), this.showWithElytra(), this.showEffects());
    }

    public static CosmeticProperties view(ItemStack stack, CosmeticProperties defaultProperties)
    {
        return new View(stack, defaultProperties);
    }

    public void update(ItemStack stack)
    {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag cosmeticTag = new CompoundTag();
        this.cosmetic().ifPresent(s -> cosmeticTag.putString("Cosmetic", s));
        cosmeticTag.putBoolean("ShowWithElytra", this.showWithElytra());
        cosmeticTag.putBoolean("ShowEffects", this.showEffects());
        tag.put("CosmeticProperties", cosmeticTag);
    }

    private static class View extends CosmeticProperties
    {
        private final ItemStack stack;
        private final CosmeticProperties defaultProperties;

        public View(ItemStack stack, CosmeticProperties defaultProperties)
        {
            this.stack = stack;
            this.defaultProperties = defaultProperties;
        }

        @Nullable
        private CompoundTag getCosmeticTag()
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(tag.contains("CosmeticProperties", Tag.TAG_COMPOUND))
            {
                return tag.getCompound("CosmeticProperties");
            }
            return null;
        }

        private CompoundTag getOrCreateCosmeticTag()
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(!tag.contains("CosmeticProperties", Tag.TAG_COMPOUND))
            {
                tag.put("CosmeticProperties", new CompoundTag());
            }
            return tag.getCompound("CosmeticProperties");
        }

        @Override
        public Optional<String> cosmetic()
        {
            CompoundTag cosmeticTag = this.getCosmeticTag();
            if(cosmeticTag == null)
                return this.defaultProperties.cosmetic();

            String id = this.getCosmeticTag().getString("Cosmetic");
            if(id.isBlank())
                return this.defaultProperties.cosmetic();

            return Optional.of(id);
        }

        @Override
        public void setCosmetic(String cosmeticId)
        {
            this.getOrCreateCosmeticTag().putString("Cosmetic", cosmeticId);
        }

        @Override
        public boolean showWithElytra()
        {
            CompoundTag cosmeticTag = this.getCosmeticTag();
            if(cosmeticTag != null && cosmeticTag.contains("ShowWithElytra", Tag.TAG_BYTE))
            {
                return cosmeticTag.getBoolean("ShowWithElytra");
            }
            return this.defaultProperties.showWithElytra();
        }

        @Override
        public void setShowWithElytra(boolean showWithElytra)
        {
            this.getOrCreateCosmeticTag().putBoolean("ShowWithElytra", showWithElytra);
        }

        @Override
        public boolean showEffects()
        {
            CompoundTag cosmeticTag = this.getCosmeticTag();
            if(cosmeticTag != null && cosmeticTag.contains("ShowEffects", Tag.TAG_BYTE))
            {
                return cosmeticTag.getBoolean("ShowEffects");
            }
            return this.defaultProperties.showEffects();
        }

        @Override
        public void setShowEffects(boolean showEffects)
        {
            this.getOrCreateCosmeticTag().putBoolean("ShowEffects", showEffects);
        }
    }
}
