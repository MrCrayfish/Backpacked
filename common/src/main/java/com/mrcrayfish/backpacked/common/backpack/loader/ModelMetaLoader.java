package com.mrcrayfish.backpacked.common.backpack.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.ModelMeta;
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
    private static final String EXTENSION = ".backpack";
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
                    Constants.LOG.error("Failed to parse backpack meta: %s".formatted(location));
                    Constants.LOG.error(result.error().get().message());
                } else {
                    String raw = location.getPath();
                    String path = location.getPath().substring((DIRECTORY + "/").length(), raw.length() - EXTENSION.length());
                    ResourceLocation key = new ResourceLocation(location.getNamespace(), path);
                    map.put(key, result.getOrThrow(false, Constants.LOG::error));
                }
            } catch(IOException e) {
                Constants.LOG.error("Failed to load backpack meta: %s".formatted(location), e);
            }
        });
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, ModelMeta> map, ResourceManager manager, ProfilerFiller filler)
    {
        BackpackManager.instance().updateModelMeta(map);
    }
}
