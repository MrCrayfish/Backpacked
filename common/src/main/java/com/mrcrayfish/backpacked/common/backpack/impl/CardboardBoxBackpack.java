package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.backpack.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.common.backpack.tracker.impl.CountProgressTracker;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class CardboardBoxBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "cardboard_box");

    public CardboardBoxBackpack()
    {
        super(null, null);
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(1000, ProgressFormatters.CUT_X_OF_X);
    }
}
