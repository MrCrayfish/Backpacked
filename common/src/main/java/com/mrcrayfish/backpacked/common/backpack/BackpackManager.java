package com.mrcrayfish.backpacked.common.backpack;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.network.Network;
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

    private Map<ResourceLocation, Backpack> registeredBackpacks = new HashMap<>();
    private Map<ResourceLocation, ModelMeta> registeredModelMeta = new HashMap<>();

    private BackpackManager() {}

    public void updateBackpacks(Map<ResourceLocation, Backpack> map)
    {
        this.registeredBackpacks = map;
    }

    public void updateModelMeta(Map<ResourceLocation, ModelMeta> map)
    {
        this.registeredModelMeta = map;
    }

    @Nullable
    public Backpack getBackpack(ResourceLocation id)
    {
        return this.registeredBackpacks.get(id);
    }

    @Nullable
    public Backpack getBackpack(String id)
    {
        return this.registeredBackpacks.get(ResourceLocation.tryParse(id));
    }

    public List<Backpack> getRegisteredBackpacks()
    {
        return ImmutableList.copyOf(this.registeredBackpacks.values());
    }

    public ModelMeta getModelMeta(ResourceLocation id)
    {
        return this.registeredModelMeta.getOrDefault(id, ModelMeta.DEFAULT);
    }

    public void unlockBackpack(ServerPlayer player, ResourceLocation id)
    {
        // Prevents unlocking backpacks when all backpacks are forcefully unlocked.
        // This helps in the case a server owner wants to revert the change.
        if(!Config.SERVER.common.unlockAllBackpacks.get())
            return;

        if(!this.registeredBackpacks.containsKey(id))
            return;

        UnlockManager.get(player).ifPresent(impl -> {
            if(impl.unlockBackpack(id)) {
                Network.getPlay().sendToPlayer(() -> player, new MessageUnlockBackpack(id));
            }
        });
    }
}
