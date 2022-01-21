package com.mrcrayfish.backpacked.common.data;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.entity.Entity;
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

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PickpocketChallenge
{
    @CapabilityInject(PickpocketChallenge.class)
    public static final Capability<PickpocketChallenge> PICKPOCKET_CAPABILITY = null;

    private final Map<PlayerEntity, Long> detectedPlayers = new HashMap<>();

    public Map<PlayerEntity, Long> getDetectedPlayers()
    {
        return this.detectedPlayers;
    }

    public static void registerCapability()
    {
        CapabilityManager.INSTANCE.register(PickpocketChallenge.class, new Storage(), PickpocketChallenge::new);
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
            return null;
        }

        @Override
        public void readNBT(Capability<PickpocketChallenge> capability, PickpocketChallenge instance, Direction side, INBT nbt) {}
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
