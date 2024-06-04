package com.mrcrayfish.backpacked.common.backpack.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BackpackLoader extends SimpleJsonResourceReloadListener
{
    private static final String DIRECTORY = "backpacked";
    private static final Gson GSON = new GsonBuilder().create();

    private final HolderLookup.Provider provider;

    public BackpackLoader(HolderLookup.Provider provider)
    {
        super(GSON, DIRECTORY);
        this.provider = provider;
    }

    protected HolderLookup.Provider getProvider()
    {
        return this.provider;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler)
    {
        // TODO check for order
        Map<ResourceLocation, Backpack> backpacks = new HashMap<>();
        map.forEach((location, element) -> {
            if(location.getPath().contains("/")) {
                Constants.LOG.warn("Ignoring backpack '{}' as it was in a sub-directory", location);
                return;
            }

            JsonObject object = element.getAsJsonObject();
            if(object.has("mod_loaded") && object.get("mod_loaded").isJsonPrimitive()) {
                String modId = object.get("mod_loaded").getAsString();
                if(!Services.REGISTRATION.isModLoaded(modId)) {
                    return;
                }
            }

            RegistryOps<JsonElement> ops = this.getProvider().createSerializationContext(JsonOps.INSTANCE);
            Backpack backpack = Backpack.CODEC.parse(ops, object).getOrThrow(s -> {
                Constants.LOG.error("An error occurred when parsing the backpack '{}'", location);
                return new JsonParseException(s);
            });
            backpack.setup(location);
            backpacks.put(location, backpack);
        });
        BackpackManager.instance().updateBackpacks(backpacks);
    }
}
