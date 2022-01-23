package com.mrcrayfish.backpacked.common.data;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PickpocketChallenge
{
    @CapabilityInject(PickpocketChallenge.class)
    public static final Capability<PickpocketChallenge> PICKPOCKET_CAPABILITY = null;

    private boolean initialized = false;
    private boolean backpack = false;
    private boolean spawnedLoot = false;
    private final Map<PlayerEntity, Long> detectedPlayers = new HashMap<>();
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

    public Map<PlayerEntity, Long> getDetectedPlayers()
    {
        return this.detectedPlayers;
    }

    public boolean isDislikedPlayer(PlayerEntity player)
    {
        return this.dislikedPlayers.containsKey(player.getUUID());
    }

    public void addDislikedPlayer(PlayerEntity player, long time)
    {
        this.dislikedPlayers.put(player.getUUID(), time);
    }

    public Map<UUID, Long> getDislikedPlayers()
    {
        return this.dislikedPlayers;
    }

    public static void registerCapability()
    {
        CapabilityManager.INSTANCE.register(PickpocketChallenge.class, new Storage(), PickpocketChallenge::new);
    }

    @SuppressWarnings("ConstantConditions")
    public static Optional<PickpocketChallenge> get(LivingEntity entity)
    {
        return entity.getCapability(PickpocketChallenge.PICKPOCKET_CAPABILITY).resolve();
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof WanderingTraderEntity)
        {
            Provider provider = new Provider();
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "pickpocket_challenge"), provider);
            event.addListener(provider::invalidate);
        }
    }

    public static class Storage implements Capability.IStorage<PickpocketChallenge>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<PickpocketChallenge> capability, PickpocketChallenge instance, Direction side)
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("Initialized", instance.initialized);
            tag.putBoolean("EquippedBackpack", instance.backpack);
            tag.putBoolean("SpawnedLoot", instance.spawnedLoot);
            return tag;
        }

        @Override
        public void readNBT(Capability<PickpocketChallenge> capability, PickpocketChallenge instance, Direction side, INBT nbt)
        {
            CompoundNBT tag = (CompoundNBT) nbt;
            instance.initialized = tag.getBoolean("Initialized");
            instance.backpack = tag.getBoolean("EquippedBackpack");
            instance.spawnedLoot = tag.getBoolean("SpawnedLoot");
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT>
    {
        private final PickpocketChallenge instance = new PickpocketChallenge();
        private final LazyOptional<PickpocketChallenge> optional = LazyOptional.of(() -> this.instance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return PICKPOCKET_CAPABILITY.orEmpty(cap, this.optional.cast());
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            return (CompoundNBT) PICKPOCKET_CAPABILITY.writeNBT(this.instance, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT tag)
        {
            PICKPOCKET_CAPABILITY.readNBT(this.instance, null, tag);
        }

        public void invalidate()
        {
            this.optional.invalidate();
        }
    }
}
