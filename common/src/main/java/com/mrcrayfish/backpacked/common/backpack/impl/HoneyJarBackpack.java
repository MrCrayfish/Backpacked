package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class HoneyJarBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "honey_jar");

    public HoneyJarBackpack()
    {
        super(ID);
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ModelInstances.get()::getHoneyJar;
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(20, ProgressFormatters.COLLECT_X_OF_X);
    }
}
