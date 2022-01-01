package com.mrcrayfish.backpacked.common.data;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class UnlockTracker
{
    @CapabilityInject(UnlockTracker.class)
    public static final Capability<UnlockTracker> UNLOCK_TRACKER_CAPABILITY = null;
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "unlock_tracker");

    private final Set<ResourceLocation> unlockedBackpacks = new HashSet<>();

    private UnlockTracker() {}

    public Set<ResourceLocation> getUnlockedBackpacks()
    {
        return ImmutableSet.copyOf(this.unlockedBackpacks);
    }

    public boolean unlockBackpack(ResourceLocation id)
    {
        return this.unlockedBackpacks.add(id);
    }

    public static void registerCapability()
    {
        CapabilityManager.INSTANCE.register(UnlockTracker.class, new Storage(), UnlockTracker::new);
    }

    @SuppressWarnings("ConstantConditions")
    public static LazyOptional<UnlockTracker> get(PlayerEntity player)
    {
        return player.getCapability(UNLOCK_TRACKER_CAPABILITY);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        Provider provider = new Provider();
        event.addCapability(ID, provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        get(event.getPlayer()).ifPresent(unlockTracker -> {
            Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    public static class Storage implements Capability.IStorage<UnlockTracker>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<UnlockTracker> capability, UnlockTracker instance, Direction side)
        {
            CompoundNBT tag = new CompoundNBT();
            ListNBT list = new ListNBT();
            instance.unlockedBackpacks.forEach(location -> {
                list.add(StringNBT.valueOf(location.toString()));
            });
            tag.put("UnlockedBackpacks", list);
            return tag;
        }

        @Override
        public void readNBT(Capability<UnlockTracker> capability, UnlockTracker instance, Direction side, INBT nbt)
        {
            instance.unlockedBackpacks.clear();
            CompoundNBT tag = (CompoundNBT) nbt;
            ListNBT list = tag.getList("UnlockedBackpacks", Constants.NBT.TAG_STRING);
            list.forEach(t -> {
                instance.unlockedBackpacks.add(ResourceLocation.tryParse(t.getAsString()));
            });
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT>
    {
        private final UnlockTracker instance = new UnlockTracker();
        private final LazyOptional<UnlockTracker> optional = LazyOptional.of(() -> this.instance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return UNLOCK_TRACKER_CAPABILITY.orEmpty(cap, this.optional.cast());
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            return (CompoundNBT) UNLOCK_TRACKER_CAPABILITY.writeNBT(this.instance, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT tag)
        {
            UNLOCK_TRACKER_CAPABILITY.readNBT(this.instance, null, tag);
        }

        public void invalidate()
        {
            this.optional.invalidate();
        }
    }
}
