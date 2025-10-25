package com.mrcrayfish.backpacked.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public record AddonMetadata(Component name, Component description, Component author, PackCompatibility assetsCompatibility, PackCompatibility dataCompatibility, PackType type)
{
    public static final String FILE_NAME = "backpacked_addon.mcmeta";

    public Pack.Metadata toVanilla()
    {
        PackCompatibility compatibility = switch(this.type) {
            case CLIENT_RESOURCES -> this.assetsCompatibility;
            case SERVER_DATA -> this.dataCompatibility;
        };
        return new Pack.Metadata(this.description, compatibility, FeatureFlagSet.of(), List.of());
    }

    public static Optional<Optional<AddonMetadata>> readAddonMetadata(PackLocationInfo info, Pack.ResourcesSupplier resourcesSupplier, PackType type)
    {
        try
        {
            try(PackResources resources = resourcesSupplier.openPrimary(info))
            {
                Optional<Optional<AddonMetadataSection>> result = readAddonMetadata(resources);
                if(result.isEmpty())
                    return Optional.empty();

                Optional<AddonMetadataSection> value = result.get();
                if(value.isEmpty())
                {
                    Constants.LOG.error("Failed to read metadata for Backpacked addon '{}'. Skipping resource", info.id());
                    return Optional.of(Optional.empty());
                }

                AddonMetadataSection section = value.get();
                if(section.addonFormat() > Constants.ADDON_FORMAT)
                {
                    Constants.LOG.error("Skipping Backpacked addon '{}' as it was designed for a newer version of Backpacked. Expected addon_format {} or lower, found {}", info.id(), Constants.ADDON_FORMAT, section.addonFormat);
                    return Optional.of(Optional.empty());
                }

                PackCompatibility assetsCompatibility = readPackCompatibility(section, PackType.CLIENT_RESOURCES);
                PackCompatibility dataCompatibility = readPackCompatibility(section, PackType.SERVER_DATA);
                return Optional.of(Optional.of(new AddonMetadata(section.name, section.description, section.author, assetsCompatibility, dataCompatibility, type)));
            }
        }
        catch(Exception exception)
        {
            Constants.LOG.warn("Failed to read addon {} metadata", info.id(), exception);
            return Optional.of(Optional.empty());
        }
    }

    private static Optional<Optional<AddonMetadataSection>> readAddonMetadata(PackResources resources) throws IOException
    {
        IoSupplier<InputStream> supplier = resources.getRootResource("backpacked_addon.mcmeta");
        if(supplier != null)
        {
            try(InputStream is = supplier.get())
            {
                return Optional.of(Optional.ofNullable(AbstractPackResources.getMetadataFromStream(AddonMetadataSection.TYPE, is)));
            }
        }
        return Optional.empty();
    }

    private static PackCompatibility readPackCompatibility(AddonMetadataSection section, PackType type)
    {
        int currentFormat = SharedConstants.getCurrentVersion().getPackVersion(type);
        int packFormat = switch(type) {
            case CLIENT_RESOURCES -> section.assetsFormat();
            case SERVER_DATA -> section.dataFormat();
        };
        if(packFormat > currentFormat) return PackCompatibility.TOO_NEW;
        if(packFormat < currentFormat) return PackCompatibility.TOO_OLD;
        return PackCompatibility.COMPATIBLE;
    }

    private record AddonMetadataSection(Component name, Component description, Component author, int addonFormat, int assetsFormat, int dataFormat)
    {
        public static final Codec<AddonMetadataSection> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ComponentSerialization.CODEC.fieldOf("name").forGetter(AddonMetadataSection::name),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(AddonMetadataSection::description),
                ComponentSerialization.CODEC.fieldOf("author").forGetter(AddonMetadataSection::author),
                Codec.INT.fieldOf("addon_format").forGetter(AddonMetadataSection::addonFormat),
                Codec.INT.fieldOf("assets_format").forGetter(AddonMetadataSection::assetsFormat),
                Codec.INT.fieldOf("data_format").forGetter(AddonMetadataSection::dataFormat)
        ).apply(builder, AddonMetadataSection::new));
        public static final MetadataSectionType<AddonMetadataSection> TYPE = MetadataSectionType.fromCodec("backpacked_addon", CODEC);
    }
}
