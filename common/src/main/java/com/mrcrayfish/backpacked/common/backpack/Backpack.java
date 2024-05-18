package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Backpack
{
    public static final Codec<Backpack> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.STRING.fieldOf("name").forGetter(backpack -> {
            return backpack.translationKey;
        }), ResourceLocation.CODEC.optionalFieldOf("model").forGetter(backpack -> {
            return backpack.model;
        })).apply(builder, Backpack::new);
    });

    private final String translationKey;
    private ResourceLocation id;
    private Optional<ResourceLocation> model;

    public Backpack(String translationKey, Optional<ResourceLocation> model)
    {
        this.translationKey = translationKey;
        this.model = model;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public String getTranslationKey()
    {
        return this.translationKey;
    }

    public Optional<ResourceLocation> getModel()
    {
        return this.model;
    }

    public boolean isUnlocked(Player player)
    {
        return UnlockManager.get(player).map(impl -> impl.getUnlockedBackpacks().contains(this.id)).orElse(false) || Config.SERVER.common.unlockAllBackpacks.get();
    }

    @Nullable
    public IProgressTracker createProgressTracker()
    {
        return null;
    }

    // Private. Called from BackpackLoader
    void setup(ResourceLocation id)
    {
        this.id = id;

        // Set the default model if none was provided
        if(this.model.isEmpty())
        {
            String name = "backpacked/backpack/" + id.getPath();
            this.model = Optional.of(new ResourceLocation(id.getNamespace(), name));
        }
    }
}
