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
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class MiniChestBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "mini_chest");

    public MiniChestBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.MINI_CHEST;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new ProgressTracker();
    }

    public static class ProgressTracker implements IProgressTracker
    {
        private static final int COUNT = 5;
        private int count;

        public void increment(ServerPlayerEntity player)
        {
            this.count++;
            this.markForCompletionTest(player);
        }

        @Override
        public boolean isComplete()
        {
            return this.count >= COUNT;
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
            return ProgressFormatters.FOUND_X_OF_X.apply(this.count, COUNT);
        }
    }
}
