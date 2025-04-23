package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.packs.PackRepositoryHelper;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryHelper
{
    @Shadow
    @Final
    @Mutable
    private Set<RepositorySource> sources;

    @Override
    public void backpacked$AddSource(RepositorySource source)
    {
        if(!(this.sources instanceof LinkedHashSet<RepositorySource>))
        {
            this.sources = new LinkedHashSet<>(this.sources);
        }
        this.sources.add(source);
    }
}
