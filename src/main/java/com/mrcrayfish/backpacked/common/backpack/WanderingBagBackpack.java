package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientHandler;
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
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class WanderingBagBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "wandering_bag");

    public WanderingBagBackpack()
    {
        super(ID);
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ClientHandler.getModelInstances()::getWanderingBag;
    }

    @Override
    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return new PickpocketProgressTracker();
    }

    public static class PickpocketProgressTracker implements IProgressTracker
    {
        private static final int COUNT = 2;
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
            ListTag list = tag.getList("PickpocketTraders", Tag.TAG_COMPOUND);
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
            tag.put("PickpocketTraders", list);
        }

        @Override
        public Component getDisplayComponent()
        {
            return ProgressFormatters.PICKPOCKETED_X_OF_X.apply(this.traders.size(), COUNT);
        }
    }
}
