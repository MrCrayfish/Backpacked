package com.mrcrayfish.backpacked.common.tracker;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public record ProgressFormatter(ResourceLocation id, BiFunction<Integer, Integer, Component> formatter)
{
    public static final BiMap<ResourceLocation, ProgressFormatter> REGISTERED_FORMATTERS = HashBiMap.create();
    public static final Codec<ProgressFormatter> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
        ProgressFormatter type = REGISTERED_FORMATTERS.get(id);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Formatter does not exist: " + id);
    }, type -> {
        ResourceLocation id = REGISTERED_FORMATTERS.inverse().get(type);
        return id != null ? DataResult.success(id) : DataResult.error(() -> "Unregistered formatter");
    });

    private static ProgressFormatter register(String name, BiFunction<Integer, Integer, Component> function)
    {
        ResourceLocation id = new ResourceLocation(Constants.MOD_ID, name);
        ProgressFormatter formatter = new ProgressFormatter(id, function);
        REGISTERED_FORMATTERS.put(id, formatter);
        return formatter;
    }

    public static final ProgressFormatter COMPLETED_X_OF_X = register("completed_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.completed_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter COLLECT_X_OF_X = register("collect_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.collected_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter FED_X_OF_X = register("fed_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.fed_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter FOUND_X_OF_X = register("found_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.found_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter USED_X_TIMES = register("used_x_times", (count, unused) -> {
        return Component.translatable("backpacked.formatter.used_x_times", count);
    });

    public static final ProgressFormatter BRED_X_OF_X = register("bred_x_of_x", (count, maxCount) -> {
        return Component.translatable("backpacked.formatter.bred_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter CUT_X_OF_X = register("cut_x_of_x", (count, maxCount) -> {
        return Component.translatable("backpacked.formatter.cut_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter SHEARED_X_OF_X = register("shear_x_of_x", (count, maxCount) -> {
        return Component.translatable("backpacked.formatter.shear_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter INT_PERCENT = register("percentage", (numerator, denominator) -> {
        int percent = (int) (100 * (double) numerator / (double) denominator);
        return Component.translatable("backpacked.formatter.int_percent", percent, "%");
    });

    public static final ProgressFormatter CRAFT_X_OF_X = register("craft_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.craft_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter PICKPOCKETED_X_OF_X = register("pickpocketed_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.pickpocketed_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter EXPLORED_X_OF_X = register("explored_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.explored_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter KILLED_X_OF_X = register("killed_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.killed_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter MINED_X_OF_X = register("mined_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.mined_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter TRADED_X_OF_X = register("traded_x_of_x", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.traded_x_of_x", count, maxCount);
    });

    public static final ProgressFormatter INCOMPLETE_COMPLETE = register("incomplete_complete", (count, maxCount) -> {
        if(count < maxCount) return Component.translatable("backpacked.formatter.incomplete");
        return Component.translatable("backpacked.formatter.complete");
    });

    public static final ProgressFormatter TRAVELLED_BLOCKS = register("travelled_blocks", (count, maxCount) -> {
        count = Mth.clamp(count, 0, maxCount);
        return Component.translatable("backpacked.formatter.travelled_blocks", Math.round(count / 100F), Math.round(maxCount / 100F));
    });
}
