package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class HoneyJarBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "honey_jar");

    public HoneyJarBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.HONEY_JAR;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new ProgressTracker();
    }

    public static class ProgressTracker implements IProgressTracker
    {
        private static final int TOTAL_COUNT = 10;
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
        public String getDisplayString()
        {
            return ProgressFormatters.X_OF_X.apply(this.count, TOTAL_COUNT);
        }
    }
}