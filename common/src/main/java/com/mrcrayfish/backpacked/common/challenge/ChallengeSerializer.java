package com.mrcrayfish.backpacked.common.challenge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * Author: MrCrayfish
 */
public abstract class ChallengeSerializer<T extends Challenge> // TODO DONE
{
    public abstract T deserialize(JsonObject object);

    public static int readCount(JsonObject object, int defaultValue)
    {
        if(object.has("count"))
        {
            int value = GsonHelper.getAsInt(object, "count");
            if(value < 1) throw new JsonParseException("Count must be positive and greater than zero");
            return value;
        }
        return defaultValue;
    }
}
