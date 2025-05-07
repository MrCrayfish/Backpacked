package com.mrcrayfish.backpacked.client.backpack.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ModelMetaLoader extends SimplePreparableReloadListener<Map<ResourceLocation, ModelMeta>>
{
    private static final String DIRECTORY = "backpacked";
    private static final String EXTENSION = ".json";
    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, EXTENSION);
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected Map<ResourceLocation, ModelMeta> prepare(ResourceManager manager, ProfilerFiller filler)
    {
        Map<ResourceLocation, ModelMeta> map = new HashMap<>();
        CONVERTER.listMatchingResources(manager).forEach((location, resource) -> {
            try(Reader reader = resource.openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                DataResult<ModelMeta> result = ModelMeta.CODEC.parse(JsonOps.INSTANCE, element);
                if(result.error().isPresent()) {
                    Constants.LOG.error("Failed to parse backpack meta: {}", location);
                    Constants.LOG.error(result.error().get().message());
                } else {
                    String raw = location.getPath();
                    String path = location.getPath().substring((DIRECTORY + "/").length(), raw.length() - EXTENSION.length());
                    ResourceLocation key = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), path);
                    map.put(key, result.getOrThrow(JsonParseException::new));
                }
            } catch(IOException e) {
                Constants.LOG.error("Failed to load backpack meta: {}", location, e);
            }
        });
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, ModelMeta> map, ResourceManager manager, ProfilerFiller filler)
    {
        ClientRegistry.instance().updateModelMeta(map);
    }
}
