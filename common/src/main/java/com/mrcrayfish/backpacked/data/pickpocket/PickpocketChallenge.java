package com.mrcrayfish.backpacked.data.pickpocket;

import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PickpocketChallenge implements Serializable
{
    private boolean initialized = false;
    private boolean backpack = false;
    private boolean spawnedLoot = false;
    private final Map<Player, Long> detectedPlayers = new HashMap<>();
    private final Map<UUID, Long> dislikedPlayers = new HashMap<>();

    public boolean isInitialized()
    {
        return this.initialized;
    }

    public void setInitialized()
    {
        this.initialized = true;
    }

    public void setBackpackEquipped(boolean equipped)
    {
        this.backpack = equipped;
    }

    public boolean isBackpackEquipped()
    {
        return this.backpack;
    }

    public boolean isLootSpawned()
    {
        return this.spawnedLoot;
    }

    public void setLootSpawned()
    {
        this.spawnedLoot = true;
    }

    public Map<Player, Long> getDetectedPlayers()
    {
        return this.detectedPlayers;
    }

    public boolean isDislikedPlayer(Player player)
    {
        return this.dislikedPlayers.containsKey(player.getUUID());
    }

    public void addDislikedPlayer(Player player, long time)
    {
        this.dislikedPlayers.put(player.getUUID(), time);
    }

    public Map<UUID, Long> getDislikedPlayers()
    {
        return this.dislikedPlayers;
    }

    @Override
    public CompoundTag serialize()
    {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Initialized", this.initialized);
        tag.putBoolean("EquippedBackpack", this.backpack);
        tag.putBoolean("SpawnedLoot", this.spawnedLoot);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag)
    {
        this.initialized = tag.getBoolean("Initialized");
        this.backpack = tag.getBoolean("EquippedBackpack");
        this.spawnedLoot = tag.getBoolean("SpawnedLoot");
    }

    public static Optional<PickpocketChallenge> get(Entity entity)
    {
        return Optional.ofNullable(Services.BACKPACK.getPickpocketChallenge(entity));
    }
}
