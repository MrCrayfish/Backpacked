package com.mrcrayfish.backpacked.common.backpack;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncBackpacks;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public final class BackpackManager
{
    private static final ResourceLocation FALLBACK_BACKPACK = new ResourceLocation(Constants.MOD_ID, "standard");

    private static BackpackManager instance;

    public static BackpackManager instance()
    {
        if(instance == null)
        {
            instance = new BackpackManager();
        }
        return instance;
    }

    private Map<ResourceLocation, Backpack> loadedBackpacks = new HashMap<>();

    // Client only
    private Map<ResourceLocation, Backpack> clientBackpacks = new HashMap<>();
    private Map<ResourceLocation, ModelMeta> clientModelMeta = new HashMap<>();

    private BackpackManager() {}

    public void updateBackpacks(Map<ResourceLocation, Backpack> map)
    {
        this.loadedBackpacks = map;
    }

    public void updateModelMeta(Map<ResourceLocation, ModelMeta> map)
    {
        this.clientModelMeta = map;
    }

    public void updateClientBackpacks(List<Backpack> backpacks)
    {
        this.clientBackpacks.clear();
        backpacks.forEach(backpack -> {
            this.clientBackpacks.put(backpack.getId(), backpack);
        });
    }

    @Nullable
    public Backpack getBackpack(ResourceLocation id)
    {
        return this.loadedBackpacks.get(id);
    }

    public List<Backpack> getBackpacks()
    {
        return ImmutableList.copyOf(this.loadedBackpacks.values());
    }

    @Nullable
    public Backpack getClientBackpack(ResourceLocation id)
    {
        return this.clientBackpacks.get(id);
    }

    @Nullable
    public Backpack getClientBackpackOrDefault(String id)
    {
        // Try getting the backpack with the given id
        Backpack backpack = this.clientBackpacks.get(ResourceLocation.tryParse(id));
        if(backpack != null)
        {
            return backpack;
        }

        // Otherwise try getting the default backpack defined by the server config
        ResourceLocation defaultId = ResourceLocation.tryParse(Config.SERVER.backpack.defaultCosmetic.get());
        backpack = this.clientBackpacks.get(defaultId);
        if(backpack != null)
        {
            return backpack;
        }

        // Otherwise try getting the fallback. If this fails, then something went really wrong
        return this.clientBackpacks.get(FALLBACK_BACKPACK);
    }

    public List<Backpack> getClientBackpacks()
    {
        return ImmutableList.copyOf(this.clientBackpacks.values());
    }

    public ModelMeta getModelMeta(Backpack backpack)
    {
        return this.clientModelMeta.getOrDefault(backpack.getId(), ModelMeta.DEFAULT);
    }

    public void unlockBackpack(ServerPlayer player, ResourceLocation id)
    {
        // Prevents unlocking backpacks when all backpacks are forcefully unlocked.
        // This helps in the case a server owner wants to revert the change.
        if(Config.SERVER.backpack.unlockAllCosmetics.get())
            return;

        if(!this.loadedBackpacks.containsKey(id))
            return;

        UnlockManager.getTracker(player).ifPresent(impl -> {
            if(impl.unlockBackpack(id)) {
                Network.getPlay().sendToPlayer(() -> player, new MessageUnlockBackpack(id));
            }
        });
    }

    public MessageSyncBackpacks getSyncMessage()
    {
        return new MessageSyncBackpacks(this.getBackpacks());
    }
}
