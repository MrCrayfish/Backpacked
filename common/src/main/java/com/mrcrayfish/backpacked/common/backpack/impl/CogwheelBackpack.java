package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.impl.UniqueCraftingProgressTracker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class CogwheelBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "cogwheel");

    public CogwheelBackpack()
    {
        super(null, null);
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new UniqueCraftingProgressTracker(30, stack -> Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).getNamespace().equals("create"));
    }
}
