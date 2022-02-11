package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.UniqueCraftingProgressTracker;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class CogwheelBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "cogwheel");

    public CogwheelBackpack()
    {
        super(ID);
    }

    @Override
    public Supplier<BackpackModel> getModelSupplier()
    {
        return () -> ModelInstances.COGWHEEL;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new UniqueCraftingProgressTracker(30, stack -> stack.getItem().getRegistryName().getNamespace().equals("create"));
    }
}
