package com.mrcrayfish.backpacked.common.backpack;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncBackpacks;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents;
import net.minecraft.resources.Identifier;
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
    private static final Identifier FALLBACK_MODEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "vintage");
    private static Identifier defaultCosmetic;
    private static BackpackManager instance;

    public static BackpackManager instance()
    {
        if(instance == null)
        {
            instance = new BackpackManager();
        }
        return instance;
    }

    private Map<Identifier, Backpack> loadedBackpacks = new HashMap<>();

    private BackpackManager()
    {
        // Update the default backpack on load/reload of config
        FrameworkConfigEvents.LOAD.register(object -> {
            if(object == Config.BACKPACK) {
                updateDefaultCosmetic();
            }
        });
        FrameworkConfigEvents.RELOAD.register(object -> {
            if(object == Config.BACKPACK) {
                updateDefaultCosmetic();
            }
        });
    }

    public void updateBackpacks(Map<Identifier, Backpack> map)
    {
        this.loadedBackpacks = map;
    }

    @Nullable
    public Backpack getBackpack(Identifier id)
    {
        return this.loadedBackpacks.get(id);
    }

    public List<Backpack> getBackpacks()
    {
        return ImmutableList.copyOf(this.loadedBackpacks.values());
    }

    public void unlockBackpack(ServerPlayer player, Identifier id)
    {
        // Prevents unlocking backpacks when all backpacks are forcefully unlocked.
        // This helps in the case a server owner wants to revert the change.
        if(Config.BACKPACK.cosmetics.unlockAllCosmetics.get())
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

    private static void updateDefaultCosmetic()
    {
        defaultCosmetic = Identifier.tryParse(Config.BACKPACK.cosmetics.defaultCosmetic.get());
    }

    @Nullable
    public static Identifier getDefaultCosmetic()
    {
        return defaultCosmetic;
    }

    public static Identifier getDefaultOrFallbackCosmetic()
    {
        return defaultCosmetic != null ? defaultCosmetic : FALLBACK_MODEL;
    }
}
