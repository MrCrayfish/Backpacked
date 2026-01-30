package com.mrcrayfish.backpacked.common.challenge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public abstract class Challenge // TODO DONE
{
    private final ResourceLocation id;

    protected Challenge(ResourceLocation id)
    {
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    @SuppressWarnings({"rawtypes"})
    public static Optional<Challenge> deserialize(@Nullable JsonElement element) throws JsonParseException
    {
        if(element != null && element.isJsonObject())
        {
            JsonObject object = element.getAsJsonObject();
            ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(object, "type"));
            ChallengeSerializer serializer = ChallengeManager.instance().getSerializer(type);
            if(serializer == null) throw new JsonParseException("Invalid challenge: " + type);
            return Optional.ofNullable(serializer.deserialize(object));
        }
        return Optional.empty();
    }

    public abstract ChallengeSerializer<?> getSerializer();

    public abstract IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId);
}
