package com.mrcrayfish.backpacked.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class UnlockTracker
{
    public static final Capability<UnlockTracker> UNLOCK_TRACKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "unlock_tracker");
    private static final Set<ServerPlayer> testForCompletion = new HashSet<>();

    private final Set<ResourceLocation> unlockedBackpacks = new HashSet<>();
    private final Map<ResourceLocation, IProgressTracker> progressTrackerMap;

    private UnlockTracker()
    {
        ImmutableMap.Builder<ResourceLocation, IProgressTracker> builder = ImmutableMap.builder();
        BackpackManager.instance().getRegisteredBackpacks().forEach(backpack ->
        {
            IProgressTracker tracker = backpack.createProgressTracker();
            if(tracker != null)
            {
                builder.put(backpack.getId(), tracker);
            }
        });
        this.progressTrackerMap = builder.build();
    }

    public Set<ResourceLocation> getUnlockedBackpacks()
    {
        return ImmutableSet.copyOf(this.unlockedBackpacks);
    }

    public boolean isUnlocked(ResourceLocation id)
    {
        return this.unlockedBackpacks.contains(id);
    }

    public Optional<IProgressTracker> getProgressTracker(ResourceLocation id)
    {
        if(!Config.SERVER.unlockAllBackpacks.get() && !this.unlockedBackpacks.contains(id))
        {
            return Optional.ofNullable(this.progressTrackerMap.get(id));
        }
        return Optional.empty();
    }

    public boolean unlockBackpack(ResourceLocation id)
    {
        if(BackpackManager.instance().getBackpack(id) != null)
        {
            return this.unlockedBackpacks.add(id);
        }
        return false;
    }

    static void queuePlayerForCompletionTest(ServerPlayer player)
    {
        testForCompletion.add(player);
    }

    public static LazyOptional<UnlockTracker> get(Player player)
    {
        return player.getCapability(UNLOCK_TRACKER_CAPABILITY);
    }

    Map<ResourceLocation, IProgressTracker> getProgressTrackerMap()
    {
        return this.progressTrackerMap;
    }

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(UnlockTracker.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
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

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        Player originalPlayer = event.getOriginal();
        originalPlayer.reviveCaps();
        get(originalPlayer).ifPresent(originalTracker ->
        {
            get(event.getPlayer()).ifPresent(newTracker ->
            {
                newTracker.unlockedBackpacks.addAll(originalTracker.unlockedBackpacks);
                originalTracker.progressTrackerMap.forEach((location, progressTracker) ->
                {
                    CompoundTag tag = new CompoundTag();
                    progressTracker.write(tag);
                    Optional.ofNullable(newTracker.progressTrackerMap.get(location)).ifPresent(t -> t.read(tag));
                });
            });
        });
        originalPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        get(event.getPlayer()).ifPresent(unlockTracker -> {
            Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        get(event.getPlayer()).ifPresent(unlockTracker -> {
            Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        get(event.getPlayer()).ifPresent(unlockTracker -> {
            Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        if(testForCompletion.isEmpty())
            return;

        for(ServerPlayer player : testForCompletion)
        {
            get(player).ifPresent(unlockTracker ->
            {
                unlockTracker.progressTrackerMap.forEach((location, progressTracker) ->
                {
                    if(!unlockTracker.unlockedBackpacks.contains(location) && progressTracker.isComplete())
                    {
                        BackpackManager.instance().unlockBackpack(player, location);
                    }
                });
            });
        }
        testForCompletion.clear();
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
            CompoundTag tag = new CompoundTag();

            ListTag unlockedBackpacks = new ListTag();
            this.instance.unlockedBackpacks.forEach(location -> unlockedBackpacks.add(StringTag.valueOf(location.toString())));
            tag.put("UnlockedBackpacks", unlockedBackpacks);

            ListTag progressTrackers = new ListTag();
            this.instance.progressTrackerMap.forEach((location, progressTracker) -> {
                CompoundTag progressTag = new CompoundTag();
                progressTag.putString("Id", location.toString());
                CompoundTag dataTag = new CompoundTag();
                progressTracker.write(dataTag);
                progressTag.put("Data", dataTag);
                progressTrackers.add(progressTag);
            });
            tag.put("ProgressTrackers", progressTrackers);

            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            this.instance.unlockedBackpacks.clear();

            ListTag unlockedBackpacks = tag.getList("UnlockedBackpacks", Tag.TAG_STRING);
            unlockedBackpacks.forEach(t -> this.instance.unlockedBackpacks.add(ResourceLocation.tryParse(t.getAsString())));

            ListTag progressTrackers = tag.getList("ProgressTrackers", Tag.TAG_COMPOUND);
            progressTrackers.forEach(t ->
            {
                CompoundTag progressTag = (CompoundTag) t;
                ResourceLocation id = new ResourceLocation(progressTag.getString("Id"));
                IProgressTracker tracker = this.instance.progressTrackerMap.get(id);
                if(tracker != null)
                {
                    CompoundTag dataTag = progressTag.getCompound("Data");
                    tracker.read(dataTag);
                }
            });
        }

        public void invalidate()
        {
            this.optional.invalidate();
        }
    }
}
