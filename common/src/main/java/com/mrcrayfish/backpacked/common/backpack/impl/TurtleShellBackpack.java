package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class TurtleShellBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "turtle_shell");

    public TurtleShellBackpack()
    {
        super(null, null);
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(20, ProgressFormatters.BRED_X_OF_X);
    }
}
