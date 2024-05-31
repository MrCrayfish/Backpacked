package com.mrcrayfish.backpacked.common.backpack;

import com.google.gson.JsonObject;
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
    private final Optional<Challenge> challenge;
    private ResourceLocation id;
    private ResourceLocation baseModel;
    private ResourceLocation strapsModel;
    private String translationKey;
    private boolean setup = false;

    public Backpack(Optional<Challenge> challenge)
    {
        this.challenge = challenge;
    }

    public Backpack(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        this.setup(id);
        this.challenge = buf.readBoolean() ? Optional.of(Challenge.DUMMY) : Optional.empty();
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
        return UnlockManager.getTracker(player).map(tracker -> tracker.isUnlocked(this.id)).orElse(false) || this.challenge.isEmpty() || Config.SERVER.backpack.unlockAllCosmetics.get();
    }

    @Nullable
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return this.challenge.map(c -> c.createProgressTracker(backpackId)).orElse(null);
    }

    // TODO switch to streamcodec in 1.20.6
    public void write(FriendlyByteBuf buf)
    {
        this.checkSetup();
        buf.writeResourceLocation(this.id);
        buf.writeBoolean(this.challenge.isPresent());
    }

    public void setup(ResourceLocation id)
    {
        if(!this.setup)
        {
            this.id = id;
            String name = "backpacked/" + id.getPath();
            this.baseModel = new ResourceLocation(id.getNamespace(), name);
            this.strapsModel = new ResourceLocation(id.getNamespace(), name + "_straps");
            this.translationKey = "backpack.%s.%s".formatted(id.getNamespace(), id.getPath());
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

    public static Backpack deserialize(JsonObject object)
    {
        if(object.has("unlock_challenge"))
        {
            return new Backpack(Challenge.deserialize(object.get("unlock_challenge")));
        }
        return new Backpack(Optional.empty());
    }
}
