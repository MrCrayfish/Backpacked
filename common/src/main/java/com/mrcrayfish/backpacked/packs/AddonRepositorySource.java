package com.mrcrayfish.backpacked.packs;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.FileUtil;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class AddonRepositorySource implements RepositorySource
{
    private final Path path;
    private final PackType type;
    private final PackSource source;

    public AddonRepositorySource(Path path, PackType type, PackSource source)
    {
        this.path = path;
        this.type = type;
        this.source = source;
    }

    @Override
    @SuppressWarnings("SimplifyOptionalCallChains")
    public void loadPacks(Consumer<Pack> consumer)
    {
        try
        {
            Constants.LOG.info("Looking for Backpacked addons in '{}'", this.path.getFileName().toString());
            int[] counter = {0};
            FileUtil.createDirectoriesSafe(this.path);
            FolderRepositorySource.discoverPacks(this.path, false, (packPath, resourcesSupplier) -> {
                String name = packPath.getFileName().toString();
                Optional<Optional<Pack>> result = tryAndReadAddonPack("file/" + name, resourcesSupplier, this.type, this.source);
                result.ifPresent(value -> value.ifPresent(pack -> {
                    consumer.accept(pack);
                    counter[0] = counter[0] + 1;
                }));
            });
            Constants.LOG.info("Found {} Backpacked addons", counter[0]);
        }
        catch(IOException e)
        {
            Constants.LOG.warn("Failed to list Backpacked addon in {}", this.path, e);
        }
    }

    public static Optional<Optional<Pack>> tryAndReadAddonPack(String id, Pack.ResourcesSupplier resourcesSupplier, PackType type, PackSource source)
    {
        // Don't load builtin packs or mods as a Backpacked addon
        if(Services.PLATFORM.isBuiltinOrModResourcePack(id, source))
            return Optional.empty();
        Optional<Optional<AddonMetadata>> result = AddonMetadata.readAddonMetadata(id, resourcesSupplier, type);
        return result.map(optional -> optional.map(metadata -> {
            return Pack.create(id, metadata.name(), true, resourcesSupplier, metadata.toVanilla(), type, Pack.Position.BOTTOM, true, source);
        }));
    }
}
