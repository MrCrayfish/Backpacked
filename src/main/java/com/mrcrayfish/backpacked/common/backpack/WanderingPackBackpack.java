package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.WanderingTrader;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class WanderingPackBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "wandering_pack");

    public WanderingPackBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ClientHandler.getModelInstances().getWanderingPack();
    }

    @Override
    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return new TradeProgressTracker();
    }

    public static class TradeProgressTracker implements IProgressTracker
    {
        private static final int COUNT = 5;
        private final Set<UUID> traders = new HashSet<>();

        public void addTrader(WanderingTrader trader, ServerPlayer player)
        {
            this.traders.add(trader.getUUID());
            this.markForCompletionTest(player);
        }

        @Override
        public boolean isComplete()
        {
            return this.traders.size() >= COUNT;
        }

        @Override
        public void read(CompoundTag tag)
        {
            this.traders.clear();
            ListTag list = tag.getList("TradedTraders", Tag.TAG_COMPOUND);
            list.forEach(t -> {
                CompoundTag uuidTag = (CompoundTag) t;
                long mostBits = uuidTag.getLong("Most");
                long leastBits = uuidTag.getLong("Least");
                this.traders.add(new UUID(mostBits, leastBits));
            });
        }

        @Override
        public void write(CompoundTag tag)
        {
            ListTag list = new ListTag();
            this.traders.forEach(uuid -> {
                CompoundTag uuidTag = new CompoundTag();
                uuidTag.putLong("Most", uuid.getMostSignificantBits());
                uuidTag.putLong("Least", uuid.getLeastSignificantBits());
                list.add(uuidTag);
            });
            tag.put("TradedTraders", list);
        }

        @Override
        public Component getDisplayComponent()
        {
            return ProgressFormatters.TRADED_X_OF_X.apply(this.traders.size(), COUNT);
        }
    }
}
