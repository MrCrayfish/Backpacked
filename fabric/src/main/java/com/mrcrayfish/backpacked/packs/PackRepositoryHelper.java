package com.mrcrayfish.backpacked.packs;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositoryHelper
{
    void backpacked$AddSource(RepositorySource source);

    static void addSource(PackRepository repository, RepositorySource source)
    {
        ((PackRepositoryHelper) repository).backpacked$AddSource(source);
    }
}
