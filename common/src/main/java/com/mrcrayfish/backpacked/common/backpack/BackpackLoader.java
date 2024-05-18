package com.mrcrayfish.backpacked.common.backpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class BackpackLoader extends SimpleJsonResourceReloadListener
{
    private static final String DIRECTORY = "backpacked/backpacks";
    private static final Gson GSON = new GsonBuilder().create();

    public BackpackLoader()
    {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler)
    {
        // TODO check for order
        Map<ResourceLocation, Backpack> backpacks = new HashMap<>();
        map.forEach((location, element) -> {
            DataResult<Backpack> result = Backpack.CODEC.parse(JsonOps.INSTANCE, element);
            if(result.error().isPresent()) {
                Constants.LOG.error("Failed to parse result json: " + location);
            } else {
                Backpack backpack = result.getOrThrow(false, Constants.LOG::error);
                backpack.setup(location);
                backpacks.put(location, backpack);
            }
        });
        BackpackManager.instance().accept(backpacks);
    }
}
