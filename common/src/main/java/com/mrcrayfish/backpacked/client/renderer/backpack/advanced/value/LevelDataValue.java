package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.LevelDataState;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.util.StringRepresentable;

import java.util.function.Function;

public record LevelDataValue(Property property) implements Value
{
    public static final Type TYPE = new Type(
        Utils.id("level_data"),
        RecordCodecBuilder.<LevelDataValue>mapCodec(builder -> builder.group(
            Property.CODEC.fieldOf("property").forGetter(o -> o.property)
        ).apply(builder, LevelDataValue::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        LevelDataState state = context.levelData();
        return state != null ? this.property.function.apply(state) : 0;
    }

    private enum Property implements StringRepresentable
    {
        TIME_OF_DAY("time_of_day", LevelDataState::timeOfDay),
        GAME_TIME("game_time", LevelDataState::gameTime),
        RAIN_LEVELS("rain_levels", LevelDataState::rainLevels),
        THUNDER_LEVELS("thunder_levels", LevelDataState::thunderLevels),
        SEA_LEVEL("sea_level", LevelDataState::seaLevel),
        MAX_LIGHT_LEVEL("max_light_level", LevelDataState::maxLightLevel);

        public static final Codec<Property> CODEC = StringRepresentable.fromEnum(Property::values);

        private final String name;
        private final Function<LevelDataState, Double> function;

        Property(String name, Function<LevelDataState, Double> function)
        {
            this.name = name;
            this.function = function;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
