package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

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
    public Supplier<BackpackModel> getModelSupplier()
    {
        return () -> ModelInstances.WANDERING_BAG;
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

        public void addTrader(WanderingTraderEntity trader, ServerPlayerEntity player)
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
        public void read(CompoundNBT tag)
        {
            this.traders.clear();
            ListNBT list = tag.getList("PickpocketTraders", Constants.NBT.TAG_COMPOUND);
            list.forEach(t -> {
                CompoundNBT uuidTag = (CompoundNBT) t;
                long mostBits = uuidTag.getLong("Most");
                long leastBits = uuidTag.getLong("Least");
                this.traders.add(new UUID(mostBits, leastBits));
            });
        }

        @Override
        public void write(CompoundNBT tag)
        {
            ListNBT list = new ListNBT();
            this.traders.forEach(uuid -> {
                CompoundNBT uuidTag = new CompoundNBT();
                uuidTag.putLong("Most", uuid.getMostSignificantBits());
                uuidTag.putLong("Least", uuid.getLeastSignificantBits());
                list.add(uuidTag);
            });
            tag.put("PickpocketTraders", list);
        }

        @Override
        public ITextComponent getDisplayComponent()
        {
            return ProgressFormatters.PICKPOCKETED_X_OF_X.apply(this.traders.size(), COUNT);
        }
    }
}
