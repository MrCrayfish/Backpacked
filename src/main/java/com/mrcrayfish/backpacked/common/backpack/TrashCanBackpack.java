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

/**
 * Author: MrCrayfish
 */
public class TrashCanBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "trash_can");

    public TrashCanBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.TRASH_CAN;
    }

    public static class ProgressTracker implements IProgressTracker
    {
        private static final int TOTAL_COUNT = 100;
        private int count;

        public void increment(ServerPlayerEntity player)
        {
            this.count++;
            this.markForCompletionTest(player);
        }

        @Override
        public boolean isComplete()
        {
            return this.count >= TOTAL_COUNT;
        }

        @Override
        public void read(CompoundNBT tag)
        {
            this.count = tag.getInt("Count");
        }

        @Override
        public void write(CompoundNBT tag)
        {
            tag.putInt("Count", this.count);
        }

        @Override
        public ITextComponent getDisplayComponent()
        {
            return ProgressFormatters.USED_X_TIMES.apply(this.count);
        }
    }
}
