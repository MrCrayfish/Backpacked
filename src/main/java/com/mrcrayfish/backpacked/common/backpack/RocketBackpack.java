package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RocketBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "rocket");

    public RocketBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.ROCKET;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new ProgressTracker();
    }

    public static class ProgressTracker implements IProgressTracker
    {
        private static final int TOTAL_DISTANCE = 50000;
        private int distance;

        public void addDistance(int distance, ServerPlayerEntity player)
        {
            this.distance += distance;
            this.markForCompletionTest(player);
        }

        @Override
        public boolean isComplete()
        {
            return this.distance >= TOTAL_DISTANCE;
        }

        @Override
        public void read(CompoundNBT tag)
        {
            this.distance = tag.getInt("Distance");
        }

        @Override
        public void write(CompoundNBT tag)
        {
            tag.putInt("Distance", this.distance);
        }

        @Override
        public ITextComponent getDisplayComponent()
        {
            return ProgressFormatters.INT_PERCENT.apply(this.distance, TOTAL_DISTANCE);
        }
    }
}
