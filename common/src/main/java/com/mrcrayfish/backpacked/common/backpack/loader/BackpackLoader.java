package com.mrcrayfish.backpacked.common.backpack.loader;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
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
                if(!Services.PLATFORM.isModLoaded(modId)) {
                    return;
                }
            }

            RegistryOps<JsonElement> ops = this.getProvider().createSerializationContext(JsonOps.INSTANCE);
            DataResult<Backpack> result = Backpack.CODEC.parse(ops, object);
            result.promotePartial(s -> {
                /* Backpacked will still register a backpack if a partial result if available. This
                 * just allows old addons to still work, however challenges might not work correctly.
                 * Backpacks will simply be marked that they errored during load. If no partial result
                 * is available, a full exception will be thrown. */
                Backpack backpack = result.getPartialOrThrow(msg -> {
                    Constants.LOG.error("Unable to load the backpack '{}'", location);
                    return new JsonParseException("Failed to load backpack the backpack '" + location + "' - " + msg);
                });
                Constants.LOG.error("An error occurred when loading the backpack '{}' - {}. It will still be registered, it just may not work correctly.", location, s);
                backpack.markErrored();
            });
            result.resultOrPartial().ifPresent(backpack -> {
                backpack.setup(location);
                backpacks.put(location, backpack);
                Constants.LOG.info("Adding backpack '{}'", location);
            });
        });
        BackpackManager.instance().updateBackpacks(backpacks);
    }
}
