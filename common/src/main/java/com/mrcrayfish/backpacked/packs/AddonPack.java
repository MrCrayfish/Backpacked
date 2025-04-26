package com.mrcrayfish.backpacked.packs;

import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;

import java.util.Optional;

public class AddonPack extends Pack
{
    private static final PackSelectionConfig SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.TOP, false);

    private final AddonMetadata metadata;

    public AddonPack(PackLocationInfo info, ResourcesSupplier supplier, AddonMetadata metadata, PackSelectionConfig config)
    {
        super(info, supplier, metadata.toVanilla(), config);
        this.metadata = metadata;
    }

    @Override
    public Component getTitle()
    {
        return this.metadata.name();
    }

    public Component getAuthor()
    {
        return this.metadata.author();
    }

    public static Optional<Optional<AddonPack>> tryAndReadAddonPack(PackLocationInfo info, ResourcesSupplier resourcesSupplier, PackType type)
    {
        // Don't load builtin packs or mods as a Backpacked addon
        if(Services.PLATFORM.isBuiltinOrModResourcePack(info))
            return Optional.empty();
        Optional<Optional<AddonMetadata>> result = AddonMetadata.readAddonMetadata(info, resourcesSupplier, type);
        return result.map(optional -> optional.map(metadata -> new AddonPack(info, resourcesSupplier, metadata, SELECTION_CONFIG)));
    }
}
