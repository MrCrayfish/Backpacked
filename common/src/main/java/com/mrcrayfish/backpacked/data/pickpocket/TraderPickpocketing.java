package com.mrcrayfish.backpacked.data.pickpocket;

import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.util.Serializable;
import com.mrcrayfish.framework.api.sync.IDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TraderPickpocketing implements Serializable
{
    public static final IDataSerializer<TraderPickpocketing> SERIALIZER = new Serializer();

    private boolean initialized = false;
    private boolean backpack = false;
    private boolean spawnedLoot = false;
    private final Map<Player, Long> detectedPlayers = new HashMap<>();
    private final Map<UUID, Long> dislikedPlayers = new HashMap<>();

    // TODO add updatable
    public TraderPickpocketing()
    {
        //this.updatable = updatable;
    }

    public boolean isInitialized()
    {
        return this.initialized;
    }

    public void setInitialized()
    {
        this.initialized = true;
        //this.updatable.markDirty(); // TODO
    }

    public void setBackpackEquipped(boolean equipped)
    {
        this.backpack = equipped;
        //this.updatable.markDirty(); // TODO
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
        //this.updatable.markDirty(); // TODO
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

    public static Optional<TraderPickpocketing> get(Entity entity)
    {
        if(entity instanceof WanderingTrader trader)
        {
            return Optional.ofNullable(ModSyncedDataKeys.TRADER_PICKPOCKETING.getValue(trader));
        }
        return Optional.empty();
    }

    private static class Serializer implements IDataSerializer<TraderPickpocketing>
    {
        @Override
        public void write(FriendlyByteBuf buf, TraderPickpocketing value)
        {
            buf.writeBoolean(value.initialized);
            buf.writeBoolean(value.backpack);
            buf.writeBoolean(value.spawnedLoot);
        }

        @Override
        @SuppressWarnings("removal")
        public TraderPickpocketing read(FriendlyByteBuf buf)
        {
            TraderPickpocketing challenge = new TraderPickpocketing();
            challenge.initialized = buf.readBoolean();
            challenge.backpack = buf.readBoolean();
            challenge.spawnedLoot = buf.readBoolean();
            return challenge;
        }

        /*@Override
        public TraderPickpocketing read(Updatable updatable, FriendlyByteBuf buf)
        {
            TraderPickpocketing challenge = new TraderPickpocketing(updatable);
            challenge.initialized = buf.readBoolean();
            challenge.backpack = buf.readBoolean();
            challenge.spawnedLoot = buf.readBoolean();
            return challenge;
        }*/

        @Override
        public Tag write(TraderPickpocketing value)
        {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("Initialized", value.initialized);
            tag.putBoolean("EquippedBackpack", value.backpack);
            tag.putBoolean("SpawnedLoot", value.spawnedLoot);
            return tag;
        }

        @Override
        @SuppressWarnings("removal")
        public TraderPickpocketing read(Tag nbt)
        {
            TraderPickpocketing challenge = new TraderPickpocketing();
            if(nbt instanceof CompoundTag tag)
            {
                challenge.initialized = tag.getBoolean("Initialized");
                challenge.backpack = tag.getBoolean("EquippedBackpack");
                challenge.spawnedLoot = tag.getBoolean("SpawnedLoot");
            }
            return challenge;
        }

        /*@Override
        public TraderPickpocketing read(Updatable updatable, Tag nbt)
        {
            TraderPickpocketing challenge = new TraderPickpocketing(updatable);
            if(nbt instanceof CompoundTag tag)
            {
                challenge.initialized = tag.getBoolean("Initialized");
                challenge.backpack = tag.getBoolean("EquippedBackpack");
                challenge.spawnedLoot = tag.getBoolean("SpawnedLoot");
            }
            return challenge;
        }*/
    }
}
