package com.mrcrayfish.backpacked.common.backpack;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.impl.*;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.framework.platform.Services;
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

    private BackpackManager() {}

    // Private. Only called from BackpackLoader
    void accept(Map<ResourceLocation, Backpack> backpacks)
    {
        this.registeredBackpacks = backpacks;
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
