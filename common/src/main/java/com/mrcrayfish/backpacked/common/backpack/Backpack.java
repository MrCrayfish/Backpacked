package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.challenge.UnlockChallenge;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Backpack
{
    public static final StreamCodec<FriendlyByteBuf, Backpack> STREAM_CODEC = StreamCodec.of((buf, backpack) -> {
        backpack.checkSetup();
        buf.writeResourceLocation(backpack.id);
        buf.writeBoolean(backpack.unlockChallenge.isPresent());
        buf.writeBoolean(backpack.error);
    }, Backpack::new);
    public static final StreamCodec<FriendlyByteBuf, List<Backpack>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity)
    );
    public static final Codec<Backpack> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(UnlockChallenge.CODEC.optionalFieldOf("unlock_challenge").forGetter(backpack -> {
            return backpack.unlockChallenge;
        })).apply(builder, Backpack::new);
    });

    private final Optional<UnlockChallenge> unlockChallenge;
    private ResourceLocation id;
    private String translationKey;
    private boolean setup = false;
    private boolean error = false;

    public Backpack(Optional<UnlockChallenge> unlockChallenge)
    {
        this.unlockChallenge = unlockChallenge;
    }

    public Backpack(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        this.setup(id);
        this.unlockChallenge = buf.readBoolean() ? Optional.of(UnlockChallenge.DUMMY) : Optional.empty();
        this.error = buf.readBoolean();
    }

    public Optional<UnlockChallenge> getUnlockChallenge()
    {
        return this.unlockChallenge;
    }

    public ResourceLocation getId()
    {
        this.checkSetup();
        return this.id;
    }

    public String getTranslationKey()
    {
        return this.translationKey;
    }

    public boolean isUnlocked(Player player)
    {
        return UnlockManager.getTracker(player).map(tracker -> tracker.isUnlocked(this.id)).orElse(false) || this.unlockChallenge.isEmpty() || Config.BACKPACK.cosmetics.unlockAllCosmetics.get();
    }

    @Nullable
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return this.unlockChallenge.map(c -> c.challenge().createProgressTracker(c.formatter(), backpackId)).orElse(null);
    }

    public void setup(ResourceLocation id)
    {
        if(!this.setup)
        {
            this.id = id;
            this.translationKey = "backpack.%s.%s".formatted(id.getNamespace(), id.getPath());
            this.setup = true;
        }
    }

    protected void checkSetup()
    {
        if(!this.setup)
        {
            throw new RuntimeException("Backpack is not setup");
        }
    }

    public void markErrored()
    {
        this.error = true;
    }

    public boolean isErrored()
    {
        return this.error;
    }
}
