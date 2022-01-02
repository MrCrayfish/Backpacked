package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

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

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(100, ProgressFormatters.USED_X_TIMES);
    }
}
