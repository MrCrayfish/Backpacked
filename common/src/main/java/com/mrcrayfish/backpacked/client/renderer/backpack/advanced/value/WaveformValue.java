package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;

/**
 * Author: MrCrayfish
 */
public record WaveformValue(Waveform waveform, double wavelength, double amplitude, double phase) implements Value
{
    public static final Type TYPE = new Type(
        Utils.rl("waveform"),
        RecordCodecBuilder.<WaveformValue>mapCodec(builder -> builder.group(
            Waveform.CODEC.fieldOf("waveform").forGetter(o -> o.waveform),
            Codec.DOUBLE.fieldOf("wavelength").orElse(2.0).forGetter(o -> o.wavelength),
            Codec.DOUBLE.fieldOf("amplitude").orElse(1.0).forGetter(o -> o.amplitude),
            Codec.DOUBLE.fieldOf("phase").orElse(0.0).forGetter(o -> o.phase)
        ).apply(builder, WaveformValue::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        double time = context.tickCount() + context.partialTick();
        return this.waveform.function.apply(time, this.wavelength, this.amplitude, this.phase);
    }

    @FunctionalInterface
    public interface WaveformFunction
    {
        double apply(double time, double wavelength, double amplitude, double phase);
    }

    public enum Waveform implements StringRepresentable
    {
        SINE("sine", (time, wavelength, amplitude, phase) -> {
            return amplitude * Mth.sin((float) ((2 * Mth.PI * time - phase) / wavelength));
        }),
        SQUARE("square", (time, wavelength, amplitude, phase) -> {
            return Mth.positiveModulo((time - phase), wavelength) < wavelength / 2 ? amplitude : -amplitude;
        }),
        TRIANGLE("triangle", (time, wavelength, amplitude, phase) -> {
            return ((2 * amplitude) / Mth.PI) * Math.asin(Mth.sin((float) (((2 * Mth.PI * time) - phase) / wavelength)));
        }),
        SAWTOOTH("sawtooth", (time, wavelength, amplitude, phase) -> {
            return ((2 * amplitude) / Mth.PI) * Math.atan(Math.tan((float) (((2 * Mth.PI * time) - phase) / (2 * wavelength))));
        });

        @SuppressWarnings("deprecation")
        public static final StringRepresentable.EnumCodec<Waveform> CODEC = StringRepresentable.fromEnum(Waveform::values);

        private final String name;
        private final WaveformFunction function;

        Waveform(String name, WaveformFunction function)
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
