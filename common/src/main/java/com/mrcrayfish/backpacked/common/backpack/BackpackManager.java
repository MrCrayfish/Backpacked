package com.mrcrayfish.backpacked.common.backpack;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
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
    public Backpack getClientBackpack(String id)
    {
        return this.clientBackpacks.get(ResourceLocation.tryParse(id));
    }

    public List<Backpack> getClientBackpacks()
    {
        return ImmutableList.copyOf(this.clientBackpacks.values());
    }

    // TODO move out
    public ModelMeta getModelMeta(ResourceLocation id)
    {
        return this.clientModelMeta.getOrDefault(id, ModelMeta.DEFAULT);
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
