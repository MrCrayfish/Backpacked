package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import net.minecraft.network.FriendlyByteBuf;
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
        }), Challenge.CODEC.optionalFieldOf("unlock_challenge").forGetter(backpack -> {
            return backpack.challenge;
        })).apply(builder, Backpack::new);
    });

    private final String translationKey;
    private final Optional<Challenge> challenge;
    private ResourceLocation id;
    private ResourceLocation baseModel;
    private ResourceLocation strapsModel;
    private boolean setup = false;

    public Backpack(String translationKey, Optional<Challenge> challenge)
    {
        this.translationKey = translationKey;
        this.challenge = challenge;
    }

    public Backpack(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        this.setup(id);
        this.translationKey = buf.readUtf(256);
        this.challenge = buf.readOptional(Challenge::read);
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

    public boolean isUnlocked(Player player)
    {
        return UnlockManager.getTracker(player).map(tracker -> tracker.isUnlocked(this.id)).orElse(false) || this.challenge.isEmpty() || Config.SERVER.common.unlockAllBackpacks.get();
    }

    @Nullable
    public IProgressTracker createProgressTracker()
    {
        return this.challenge.map(Challenge::createProgressTracker).orElse(null);
    }

    // TODO switch to streamcodec in 1.20.6
    public void write(FriendlyByteBuf buf)
    {
        this.checkSetup();
        buf.writeResourceLocation(this.id);
        buf.writeUtf(this.translationKey, 256);
        buf.writeOptional(this.challenge, (b, c) -> c.write(b));
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

    private void checkSetup()
    {
        if(!this.setup)
        {
            throw new RuntimeException("Backpack is not setup");
        }
    }
}
