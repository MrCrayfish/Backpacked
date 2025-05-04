package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

public record LevelDataValue(Property property) implements Value
{
    public static final Type TYPE = new Type(
        Utils.rl("level_data"),
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
        Level level = context.level();
        if(level == null)
            return 0;
        return this.property.function.apply(level, context);
    }

    private enum Property implements StringRepresentable
    {
        TIME_OF_DAY("time_of_day", (level, context) -> level.getTimeOfDay(context.partialTick()));

        public static final Codec<Property> CODEC = StringRepresentable.fromEnum(Property::values);

        private final String name;
        private final BiFunction<Level, BackpackRenderContext, Float> function;

        Property(String name, BiFunction<Level, BackpackRenderContext, Float> function)
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
