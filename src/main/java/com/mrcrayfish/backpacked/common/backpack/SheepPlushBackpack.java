package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SheepPlushBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "sheep_plush");

    public SheepPlushBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.SHEEP_PLUSH;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(250, ProgressFormatters.SHEARED_X_SHEEP);
    }
}
