package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class MiniChestBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "mini_chest");

    public MiniChestBackpack()
    {
        super(null, null);
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(5, ProgressFormatters.FOUND_X_OF_X);
    }
}
