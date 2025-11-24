package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.ShelfKey;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record RecallAugment(Optional<ShelfKey> shelfKey) implements Augment<RecallAugment>
{
    public static final RecallAugment EMPTY = new RecallAugment(Optional.empty());
    public static final AugmentType<RecallAugment> TYPE = new AugmentType<>(
        Utils.rl("recall"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShelfKey.CODEC.optionalFieldOf("shelf_key").forGetter(RecallAugment::shelfKey)
        ).apply(instance, RecallAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.optional(ShelfKey.STREAM_CODEC), RecallAugment::shelfKey,
            RecallAugment::new
        ),
        () -> new RecallAugment(Optional.empty())
    );
    public static final int UPDATE_SHELF_RANGE_SQR = 16 * 16;

    @Override
    public AugmentType<RecallAugment> type()
    {
        return TYPE;
    }

    @Override
    public RecallAugment onUpdate(ServerPlayer player, Augment<?> current)
    {
        RecallAugment updated = this;
        RecallAugment other = (current instanceof RecallAugment a) ? a : EMPTY;

        // When updating the shelf, we need to check if the player is within distance
        if(this.shelfKey.isPresent() && !Objects.equals(this.shelfKey, other.shelfKey))
        {
            ShelfKey shelfKey = this.shelfKey.get();

            // Server should be present
            MinecraftServer server = player.getServer();
            assert server != null;

            ServerLevel level = server.getLevel(shelfKey.level());
            if(level != null)
            {
                BlockPos pos = BlockPos.of(shelfKey.position());
                if(!level.isLoaded(pos))
                {
                    updated = updated.setShelfKey(null);
                }
                else if(level.getBlockEntity(pos, ModBlockEntities.SHELF.get()).isEmpty())
                {
                    updated = updated.setShelfKey(null);
                }
                else if(pos.distToCenterSqr(player.getEyePosition()) > UPDATE_SHELF_RANGE_SQR)
                {
                    updated = updated.setShelfKey(null);
                }

                if(updated.shelfKey().isPresent())
                {
                    Recall recall = ((Recall.Access) level).backpacked$getRecall();
                    if(!recall.isShelfAtBlockPos(pos))
                    {
                        updated = updated.setShelfKey(null);
                    }
                }
            }
            else
            {
                updated = updated.setShelfKey(null);
            }
        }
        return updated;
    }

    public RecallAugment setShelfKey(@Nullable ShelfKey shelfKey)
    {
        return new RecallAugment(Optional.ofNullable(shelfKey));
    }

}
