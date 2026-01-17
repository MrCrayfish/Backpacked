package com.mrcrayfish.backpacked.common.backpack.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BackpackLoader extends SimplePreparableReloadListener<Map<Identifier, Backpack>>
{
    public static final Identifier ID = Utils.id("backpack_loader");
    private static final FileToIdConverter DIRECTORY = FileToIdConverter.json("backpacked");

    private final HolderLookup.Provider provider;

    public BackpackLoader(HolderLookup.Provider provider)
    {
        this.provider = provider;
    }

    protected HolderLookup.Provider getProvider()
    {
        return this.provider;
    }

    @Override
    protected Map<Identifier, Backpack> prepare(ResourceManager manager, ProfilerFiller filler)
    {
        Map<Identifier, Backpack> backpacks = new HashMap<>();
        for(Map.Entry<Identifier, Resource> entry : DIRECTORY.listMatchingResources(manager).entrySet())
        {
            Identifier fileId = entry.getKey();
            Identifier backpackId = DIRECTORY.fileToId(fileId);
            if(backpackId.getPath().contains("/"))
            {
                Constants.LOG.warn("Ignoring backpack '{}' as it was in a sub-directory", backpackId);
                continue;
            }

            try(Reader reader = entry.getValue().openAsReader())
            {
                JsonElement element = StrictJsonParser.parse(reader);
                JsonObject object = element.getAsJsonObject();
                if(object.has("mod_loaded") && object.get("mod_loaded").isJsonPrimitive())
                {
                    String modId = object.get("mod_loaded").getAsString();
                    if(!Services.PLATFORM.isModLoaded(modId))
                    {
                        continue;
                    }
                }

                RegistryOps<JsonElement> ops = this.getProvider().createSerializationContext(JsonOps.INSTANCE);
                DataResult<Backpack> result = Backpack.CODEC.parse(ops, object);
                result.promotePartial(s -> {
                    /* Backpacked will still register a backpack if a partial result if available. This
                     * just allows old addons to still work, however challenges might not work correctly.
                     * Backpacks will simply be marked that they errored during load. If no partial result
                     * is available, a full exception will be thrown. */
                    Backpack backpack = result.getPartialOrThrow(msg -> new JsonParseException("Failed to load backpack the backpack '" + backpackId + "': " + msg));
                    Constants.LOG.error("An error occurred when loading the backpack '{}': {}. It will still be registered, it just may not work correctly.", backpackId, s);
                    backpack.markErrored();
                });
                result.resultOrPartial().ifPresent(backpack -> {
                    backpack.setup(backpackId);
                    backpacks.put(backpackId, backpack);
                    Constants.LOG.info("Adding backpack '{}'", backpackId);
                });
            }
            catch(IOException | JsonParseException | IllegalArgumentException exception)
            {
                Constants.LOG.error("Unable to parse backpack file '{}' from '{}'", backpackId, fileId, exception);
            }
        }
        return backpacks;
    }

    @Override
    protected void apply(Map<Identifier, Backpack> backpacks, ResourceManager manager, ProfilerFiller filler)
    {
        BackpackManager.instance().updateBackpacks(backpacks);
    }
}
