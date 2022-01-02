package com.mrcrayfish.backpacked.common;

import net.minecraft.util.math.MathHelper;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ProgressFormatters
{
    public static final BiFunction<Integer, Integer, String> X_OF_X = (count, maxCount) -> {
        count = MathHelper.clamp(count, 0, maxCount);
        return count + "/" + maxCount;
    };
}
