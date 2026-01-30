package com.mrcrayfish.backpacked.common.backpack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.UnlockChallenge;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Backpack // TODO DONE
{
    private final Optional<UnlockChallenge> unlockChallenge;
    private ResourceLocation id;
    private String translationKey;
    private boolean setup = false;
    private boolean error = false;

    public Backpack(Optional<UnlockChallenge> unlockChallenge)
    {
        this.unlockChallenge = unlockChallenge;
    }

    public Backpack(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        this.setup(id);
        this.unlockChallenge = buf.readBoolean() ? Optional.of(UnlockChallenge.DUMMY) : Optional.empty();
        this.error = buf.readBoolean();
    }

    public Optional<UnlockChallenge> getUnlockChallenge()
    {
        return this.unlockChallenge;
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

    public boolean isUnlocked(Player player)
    {
        return UnlockManager.getTracker(player).map(tracker -> tracker.isUnlocked(this.id)).orElse(false) || this.unlockChallenge.isEmpty() || Config.SERVER.backpack.unlockAllCosmetics.get();
    }

    @Nullable
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return this.unlockChallenge.map(c -> c.challenge().createProgressTracker(c.formatter(), backpackId)).orElse(null);
    }

    public void write(FriendlyByteBuf buf)
    {
        this.checkSetup();
        buf.writeResourceLocation(this.id);
        buf.writeBoolean(this.unlockChallenge.isPresent());
        buf.writeBoolean(this.error);
    }

    public void setup(ResourceLocation id)
    {
        if(!this.setup)
        {
            this.id = id;
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

    public void markErrored()
    {
        this.error = true;
    }

    public boolean isErrored()
    {
        return this.error;
    }

    public static Backpack deserialize(JsonObject object) throws JsonParseException
    {
        return new Backpack(readUnlockChallenge(object));
    }

    private static Optional<UnlockChallenge> readUnlockChallenge(JsonObject object) throws JsonParseException
    {
        if(object.has("unlock_challenge"))
        {
            JsonObject unlockChallengeObject = object.getAsJsonObject("unlock_challenge");
            return readChallenge(unlockChallengeObject).map(challenge -> {
                ProgressFormatter formatter = readFormatter(unlockChallengeObject);
                return new UnlockChallenge(formatter, challenge);
            });
        }
        return Optional.empty();
    }

    private static Optional<Challenge> readChallenge(JsonObject object) throws JsonParseException
    {
        if(object.has("challenge"))
        {
            return Challenge.deserialize(object.get("challenge"));
        }
        return Optional.empty();
    }

    private static ProgressFormatter readFormatter(JsonObject object) throws JsonParseException
    {
        if(object.has("formatter"))
        {
            ResourceLocation formatterId = new ResourceLocation(GsonHelper.getAsString(object, "formatter"));
            ProgressFormatter newFormatter = ProgressFormatter.REGISTERED_FORMATTERS.get(formatterId);
            if(newFormatter != null)
                return newFormatter;
            throw new JsonParseException("Invalid formatter: " + formatterId);
        }
        return ProgressFormatter.INCOMPLETE_COMPLETE;
    }
}
