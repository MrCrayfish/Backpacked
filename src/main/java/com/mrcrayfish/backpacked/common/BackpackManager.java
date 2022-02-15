package com.mrcrayfish.backpacked.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.*;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUnlockBackpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BackpackManager
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

    private final Map<ResourceLocation, Backpack> registeredBackpacks = new HashMap<>();

    private BackpackManager()
    {
        this.register(new BambooBasketBackpack());
        this.register(new CardboardBoxBackpack());
        this.register(new ClassicBackpack());
        this.register(new EndCrystalBackpack());
        this.register(new HoneyJarBackpack());
        this.register(new MiniChestBackpack());
        this.register(new PiglinPackBackpack());
        this.register(new RocketBackpack());
        this.register(new SheepPlushBackpack());
        this.register(new StandardBackpack());
        this.register(new TrashCanBackpack());
        this.register(new TurtleShellBackpack());
        this.register(new WanderingBagBackpack());
        this.registerDependant("create", new CogwheelBackpack());
    }

    public synchronized void register(Backpack backpack)
    {
        this.registeredBackpacks.computeIfAbsent(backpack.getId(), location -> {
            MinecraftForge.EVENT_BUS.register(backpack);
            return backpack;
        });
    }

    private synchronized void registerDependant(String mod, Backpack backpack)
    {
        if(!ModList.get().isLoaded(mod))
            return;
        this.registeredBackpacks.computeIfAbsent(backpack.getId(), location -> {
            MinecraftForge.EVENT_BUS.register(backpack);
            return backpack;
        });
    }

    public List<Backpack> getRegisteredBackpacks()
    {
        return ImmutableList.copyOf(this.registeredBackpacks.values());
    }

    public Backpack getBackpack(ResourceLocation id)
    {
        return this.registeredBackpacks.get(id);
    }

    public void unlockBackpack(ServerPlayer player, ResourceLocation id)
    {
        // Prevents unlocking backpacks when all backpacks are forcefully unlocked. This helps in case
        // the server owner wants to revert their change.
        if(Config.SERVER.unlockAllBackpacks.get())
            return;

        if(this.registeredBackpacks.containsKey(id))
        {
            UnlockTracker.get(player).ifPresent(impl ->
            {
                if(impl.unlockBackpack(id))
                {
                    Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> player), new MessageUnlockBackpack(id));
                }
            });
        }
    }
}
