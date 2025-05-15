package com.mrcrayfish.backpacked.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public enum MovementType implements StringRepresentable
{
    WALK("walk"),
    SPRINT("sprint"),
    SNEAK("sneak"),
    SWIM("swim"),
    WALK_UNDERWATER("walk_underwater"),
    WALK_ON_WATER("walk_on_water"),
    CLIMB("climb"),
    ELYTRA_FLY("elytra_flying"),
    FLY("fly"),
    FALL("fall"),
    VEHICLE("vehicle");

    public static final Map<String, MovementType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(MovementType::getSerializedName, Function.identity()));
    public static final Codec<MovementType> CODEC = StringRepresentable.fromEnum(MovementType::values);
    public static final Codec<List<MovementType>> LIST_CODEC = Codec.either(CODEC, CODEC.listOf()).xmap(either -> {
        return either.map(List::of, Function.identity());
    }, list -> {
        return list.size() == 1 ? Either.left(list.get(0)) : Either.right(list);
    });

    private final String name;

    MovementType(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }
}
