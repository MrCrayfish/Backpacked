package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.ModelSupplier;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.entity.passive.PandaEntity;
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

/**
 * Author: MrCrayfish
 */
public class BambooBasketBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "bamboo_basket");

    public BambooBasketBackpack()
    {
        super(ID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ModelSupplier getModelSupplier() {
        return () -> ModelInstances.BAMBOO_BASKET;
    }

    @Override
    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return new ProgressTracker();
    }

    public static class ProgressTracker implements IProgressTracker
    {
        private static final int COUNT = 10;
        private final Set<UUID> fedPandas = new HashSet<>();

        public void addPanda(PandaEntity panda, ServerPlayerEntity player)
        {
            this.fedPandas.add(panda.getUUID());
            this.markForCompletionTest(player);
        }

        @Override
        public boolean isComplete()
        {
            return this.fedPandas.size() >= COUNT;
        }

        @Override
        public void read(CompoundNBT tag)
        {
            this.fedPandas.clear();
            ListNBT list = tag.getList("FedPandas", Constants.NBT.TAG_COMPOUND);
            list.forEach(t -> {
                CompoundNBT uuidTag = (CompoundNBT) t;
                long mostBits = uuidTag.getLong("Most");
                long leastBits = uuidTag.getLong("Least");
                this.fedPandas.add(new UUID(mostBits, leastBits));
            });
        }

        @Override
        public void write(CompoundNBT tag)
        {
            ListNBT list = new ListNBT();
            this.fedPandas.forEach(uuid -> {
                CompoundNBT uuidTag = new CompoundNBT();
                uuidTag.putLong("Most", uuid.getMostSignificantBits());
                uuidTag.putLong("Least", uuid.getLeastSignificantBits());
                list.add(uuidTag);
            });
            tag.put("FedPandas", list);
        }

        @Override
        public ITextComponent getDisplayComponent()
        {
            return ProgressFormatters.FED_X_OF_X.apply(this.fedPandas.size(), COUNT);
        }
    }
}
