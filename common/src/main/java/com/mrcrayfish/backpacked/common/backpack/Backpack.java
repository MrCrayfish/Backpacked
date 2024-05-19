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
public class Backpack
{
    public static final Codec<Backpack> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.STRING.fieldOf("name").forGetter(backpack -> {
            return backpack.translationKey;
        })).apply(builder, Backpack::new);
    });

    private final String translationKey;
    private ResourceLocation id;
    private ResourceLocation baseModel;
    private ResourceLocation strapsModel;
    private boolean setup = false;

    public Backpack(String translationKey)
    {
        this.translationKey = translationKey;
    }

    public ResourceLocation getId()
    {
        this.checkSetup();
        return this.id;
    }

    public String getTranslationKey()
    {
        return this.translationKey;
    }

    public ResourceLocation getBaseModel()
    {
        this.checkSetup();
        return this.baseModel;
    }

    public ResourceLocation getStrapsModel()
    {
        this.checkSetup();
        return this.strapsModel;
    }

    public ModelMeta getModelMeta()
    {
        return BackpackManager.instance().getModelMeta(this.id);
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

    public void setup(ResourceLocation id)
    {
        if(!this.setup)
        {
            this.id = id;
            String name = "backpacked/" + id.getPath();
            this.baseModel = new ResourceLocation(id.getNamespace(), name);
            this.strapsModel = new ResourceLocation(id.getNamespace(), name + "_straps");
            this.setup = true;
        }
    }

    private boolean checkSetup()
    {
        if(!this.setup)
            throw new RuntimeException("Backpack is not setup");
        return true;
    }
}
