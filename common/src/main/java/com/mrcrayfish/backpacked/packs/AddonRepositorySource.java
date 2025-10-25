package com.mrcrayfish.backpacked.packs;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class AddonRepositorySource implements RepositorySource
{
    private final Path path;
    private final PackType type;
    private final PackSource source;
    private final DirectoryValidator validator;

    public AddonRepositorySource(Path path, PackType type, PackSource source, DirectoryValidator validator)
    {
        this.path = path;
        this.type = type;
        this.source = source;
        this.validator = validator;
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
            FolderRepositorySource.discoverPacks(this.path, this.validator, (packPath, resourcesSupplier) -> {
                String name = packPath.getFileName().toString();
                PackLocationInfo info = new PackLocationInfo("file/" + name, Component.literal(name), this.source, Optional.empty());
                Optional<Optional<AddonPack>> result = AddonPack.tryAndReadAddonPack(info, resourcesSupplier, this.type);
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
}
