package com.mrcrayfish.backpacked.data.tracker;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgeUnlockTracker
{
    public static final Capability<UnlockTracker> UNLOCK_TRACKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "unlock_tracker");

    public static void registerCapability(RegisterCapabilitiesEvent event)
    {
        event.register(UnlockTracker.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();
        if(entity instanceof Player)
        {
            Provider provider = new Provider();
            event.addCapability(ID, provider);
            if(!(entity instanceof ServerPlayer)) //Temp fix until Forge fix bug
            {
                event.addListener(provider::invalidate);
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag>
    {
        private final UnlockTracker instance = new UnlockTracker();
        private final LazyOptional<UnlockTracker> optional = LazyOptional.of(() -> this.instance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return UNLOCK_TRACKER_CAPABILITY.orEmpty(cap, this.optional);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return this.instance.serialize();
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            this.instance.deserialize(tag);
        }

        public void invalidate()
        {
            this.optional.invalidate();
        }
    }
}
