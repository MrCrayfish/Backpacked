package com.mrcrayfish.backpacked.common;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ProgressFormatters
{
    public static final BiFunction<Integer, Integer, Component> COLLECT_X_OF_X = (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.collected_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> FED_X_OF_X = (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.fed_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> FOUND_X_OF_X = (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.found_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> USED_X_TIMES = (count, unused) -> {
        return Component.translatable("backpacked.formatter.used_x_times", count);
    };

    public static final BiFunction<Integer, Integer, Component> BRED_X_OF_X = (count, maxCount) -> {
        return Component.translatable("backpacked.formatter.bred_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> CUT_X_OF_X = (count, maxCount) -> {
        return Component.translatable("backpacked.formatter.cut_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> SHEARED_X_SHEEP = (count, maxCount) -> {
        return Component.translatable("backpacked.formatter.shear_x_sheep", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> INT_PERCENT = (numerator, denominator) -> {
        int percent = (int) (100 * (double) numerator / (double) denominator);
        return Component.translatable("backpacked.formatter.int_percent", percent, "%");
    };

    public static final BiFunction<Integer, Integer, Component> CRAFT_X_OF_X = (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.craft_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> PICKPOCKETED_X_OF_X = (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.pickpocketed_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> EXPLORED_X_OF_X = (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.explored_x_of_x", count, maxCount);
    };

    public static final BiFunction<Integer, Integer, Component> INCOMPLETE_COMPLETE = (count, maxCount) -> {
        if(count < maxCount) return Component.translatable("backpacked.formatter.incomplete");
        return Component.translatable("backpacked.formatter.complete");
    };
}
