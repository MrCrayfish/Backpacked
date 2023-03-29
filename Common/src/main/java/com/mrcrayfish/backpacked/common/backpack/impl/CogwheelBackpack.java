package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.impl.UniqueCraftingProgressTracker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class CogwheelBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "cogwheel");

    public CogwheelBackpack()
    {
        super(ID);
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ModelInstances.get()::getCogwheel;
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new UniqueCraftingProgressTracker(30, stack -> Objects.requireNonNull(Registry.ITEM.getKey(stack.getItem())).getNamespace().equals("create"));
    }
}
