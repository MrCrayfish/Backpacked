package com.mrcrayfish.backpacked.data.pickpocket;

import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import com.mrcrayfish.framework.api.sync.SyncedObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TraderPickpocketing extends SyncedObject
{
    public static final StreamCodec<RegistryFriendlyByteBuf, TraderPickpocketing> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        TraderPickpocketing::isInitialized,
        ByteBufCodecs.BOOL,
        TraderPickpocketing::isBackpackEquipped,
        ByteBufCodecs.BOOL,
        TraderPickpocketing::isLootSpawned,
        TraderPickpocketing::new
    );
    public static final DataSerializer<TraderPickpocketing> SERIALIZER = new DataSerializer<>(STREAM_CODEC, TraderPickpocketing::write, TraderPickpocketing::read);

    private boolean initialized = false;
    private boolean backpack = false;
    private boolean spawnedLoot = false;
    private final Map<Player, Long> detectedPlayers = new HashMap<>();
    private final Map<UUID, Long> dislikedPlayers = new HashMap<>();

    public TraderPickpocketing() {}

    public TraderPickpocketing(boolean initialized, boolean backpack, boolean spawnedLoot)
    {
        this.initialized = initialized;
        this.backpack = backpack;
        this.spawnedLoot = spawnedLoot;
    }

    public boolean isInitialized()
    {
        return this.initialized;
    }

    public void setInitialized()
    {
        this.initialized = true;
        this.markDirty();
    }

    public void setBackpackEquipped(boolean equipped)
    {
        this.backpack = equipped;
        this.markDirty();
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
        this.markDirty();
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

    private Tag write(HolderLookup.Provider provider)
    {
        CompoundTag data = new CompoundTag();
        data.putBoolean("Initialized", this.initialized);
        data.putBoolean("EquippedBackpack", this.backpack);
        data.putBoolean("SpawnedLoot", this.spawnedLoot);
        return data;
    }

    private static TraderPickpocketing read(Tag tag, HolderLookup.Provider provider)
    {
        CompoundTag data = (CompoundTag) tag;
        TraderPickpocketing pickpocketing = new TraderPickpocketing();
        pickpocketing.initialized = data.getBoolean("Initialized");
        pickpocketing.backpack = data.getBoolean("EquippedBackpack");
        pickpocketing.spawnedLoot = data.getBoolean("SpawnedLoot");
        return pickpocketing;
    }

    public static Optional<TraderPickpocketing> get(Entity entity)
    {
        if(entity instanceof WanderingTrader trader)
        {
            return Optional.ofNullable(ModSyncedDataKeys.TRADER_PICKPOCKETING.getValue(trader));
        }
        return Optional.empty();
    }
}
