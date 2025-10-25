package com.mrcrayfish.backpacked.client;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ClientRegistry
{
    private static ClientRegistry instance;

    public static ClientRegistry instance()
    {
        if(instance == null)
        {
            instance = new ClientRegistry();
        }
        return instance;
    }

    private Map<ResourceLocation, ClientBackpack> backpacks = new HashMap<>();
    private Map<ResourceLocation, ModelMeta> modelMetaMap = new HashMap<>();

    public void updateBackpacks(List<Backpack> backpacks)
    {
        this.backpacks.clear();
        backpacks.forEach(backpack -> {
            this.backpacks.put(backpack.getId(), new ClientBackpack(backpack));
        });
    }

    @Nullable
    public ClientBackpack getBackpack(ResourceLocation id)
    {
        return this.backpacks.get(id);
    }

    @Nullable
    public ClientBackpack getBackpackOrDefault(ResourceLocation id)
    {
        // Try getting the backpack with the given id
        ClientBackpack backpack = this.backpacks.get(id);
        if(backpack != null)
        {
            return backpack;
        }
        // Otherwise get the default cosmetic.
        return this.backpacks.get(BackpackManager.getDefaultOrFallbackCosmetic());
    }

    public List<ClientBackpack> getBackpacks()
    {
        return ImmutableList.copyOf(this.backpacks.values());
    }

    public void updateModelMeta(Map<ResourceLocation, ModelMeta> map)
    {
        this.modelMetaMap = map;
    }

    public ModelMeta getModelMeta(Backpack backpack)
    {
        return this.modelMetaMap.getOrDefault(backpack.getId(), ModelMeta.DEFAULT);
    }
}
