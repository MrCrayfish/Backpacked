package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.impl.DummyChallenge;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
        buf.writeBoolean(backpack.challenge.isPresent());
    }, Backpack::new);
    public static final StreamCodec<FriendlyByteBuf, List<Backpack>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity)
    );
    public static final Codec<Backpack> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Challenge.CODEC.optionalFieldOf("unlock_challenge").forGetter(backpack -> {
            return backpack.challenge;
        })).apply(builder, Backpack::new);
    });

    private final Optional<Challenge> challenge;
    private ResourceLocation id;
    private ModelResourceLocation baseModel;
    private ModelResourceLocation strapsModel;
    private String translationKey;
    private boolean setup = false;

    public Backpack(Optional<Challenge> challenge)
    {
        this.challenge = challenge;
    }

    public Backpack(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        this.setup(id);
        this.challenge = buf.readBoolean() ? Optional.of(DummyChallenge.INSTANCE) : Optional.empty();
    }

    public Optional<Challenge> getChallenge()
    {
        return this.challenge;
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

    public ModelResourceLocation getBaseModel()
    {
        this.checkSetup();
        return this.baseModel;
    }

    public ModelResourceLocation getStrapsModel()
    {
        this.checkSetup();
        return this.strapsModel;
    }

    public boolean isUnlocked(Player player)
    {
        return UnlockManager.getTracker(player).map(tracker -> tracker.isUnlocked(this.id)).orElse(false) || this.challenge.isEmpty() || Config.SERVER.backpack.unlockAllCosmetics.get();
    }

    @Nullable
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return this.challenge.map(c -> c.createProgressTracker(backpackId)).orElse(null);
    }

    public void setup(ResourceLocation id)
    {
        if(!this.setup)
        {
            this.id = id;
            String name = "backpacked/" + id.getPath();
            this.baseModel = FrameworkClientAPI.createModelResourceLocation(id.getNamespace(), name);
            this.strapsModel = FrameworkClientAPI.createModelResourceLocation(id.getNamespace(), name + "_straps");
            this.translationKey = "backpack.%s.%s".formatted(id.getNamespace(), id.getPath());
            this.setup = true;
        }
    }

    private void checkSetup()
    {
        if(!this.setup)
        {
            throw new RuntimeException("Backpack is not setup");
        }
    }
}
