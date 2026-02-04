package com.mrcrayfish.backpacked.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BackpackedModelLoadingPlugin implements ModelLoadingPlugin
{
    @Override
    public void onInitializeModelLoader(Context context)
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> models = manager.listResources("models/backpacked", location -> location.getPath().endsWith(".json"));
        models.forEach((key, resource) -> {
            String path = key.getPath().substring("models/".length(), key.getPath().length() - ".json".length());
            context.addModels(new ResourceLocation(key.getNamespace(), path));
        });
    }
}
