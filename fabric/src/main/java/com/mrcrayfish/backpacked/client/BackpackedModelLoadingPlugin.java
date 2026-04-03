package com.mrcrayfish.backpacked.client;

/*import com.mrcrayfish.framework.api.client.model.FabricModelResource;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;*/

import com.mrcrayfish.framework.api.client.model.FabricModelResource;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BackpackedModelLoadingPlugin implements ModelLoadingPlugin
{
    @Override
    public void initialize(Context context)
    {
        Map<Identifier, FrameworkModelResource<FrameworkBakedModel>> loadedModels = new HashMap<>();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<Identifier, Resource> models = manager.listResources("models/backpacked", location -> location.getPath().endsWith(".json"));
        models.forEach((id, resource) -> {
            String path = id.getPath().substring("models/".length(), id.getPath().length() - ".json".length());
            Identifier standaloneId = Identifier.fromNamespaceAndPath(id.getNamespace(), path);
            FrameworkModelResource<FrameworkBakedModel> modelResource = FrameworkModelResource.create(standaloneId);
            var key = ((FabricModelResource<FrameworkBakedModel>) modelResource).extraModelKey();
            var baker = ((FabricModelResource<FrameworkBakedModel>) modelResource).unbakedModel();
            context.addModel(key, baker);
            loadedModels.put(standaloneId, modelResource);
        });
        StandaloneModels.init(loadedModels);
    }
}
