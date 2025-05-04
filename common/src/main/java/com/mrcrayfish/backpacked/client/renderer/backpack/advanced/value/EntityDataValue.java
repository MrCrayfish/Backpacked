package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;

public record EntityDataValue(Data data) implements Value
{
    public static final Type TYPE = new Type(
        Utils.rl("entity_data"),
        RecordCodecBuilder.<EntityDataValue>mapCodec(builder -> builder.group(
            Data.CODEC.fieldOf("property").forGetter(o -> o.data)
        ).apply(builder, EntityDataValue::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        LivingEntity entity = context.entity();
        if(entity == null)
            return 0;
        return this.data.function.apply(entity, context);
    }

    private enum Data implements StringRepresentable
    {
        WALK_POSITION("walk_position", (entity, context) -> (double) entity.walkAnimation.position(context.partialTick())),
        WALK_SPEED("walk_speed", (entity, context) -> (double) entity.walkAnimation.speed(context.partialTick())),
        HEAD_YAW("head_yaw", (entity, context) -> (double) entity.getViewXRot(context.partialTick())),
        HEAD_PITCH("head_pitch", (entity, context) -> (double) entity.getViewYRot(context.partialTick())),
        BODY_ROTATION("body_rotation", (entity, context) -> (double) Mth.rotLerp(context.partialTick(), entity.yBodyRotO, entity.yBodyRot)),
        SWIM_ANGLE("swimming", (entity, context) -> (double) entity.getSwimAmount(context.partialTick())),
        ATTACK_SWING("attack_swing", (entity, context) -> (double) entity.getAttackAnim(context.partialTick())),
        FALL_FLYING_COUNTER("fall_flying_counter", (entity, context) -> entity.isFallFlying() ? (double) entity.getFallFlyingTicks() + context.partialTick() : 0),
        HEALTH("health", (entity, context) -> (double) entity.getHealth()),
        MAX_HEALTH("max_health", (entity, context) -> (double) entity.getMaxHealth()),
        ABSORPTION("absorption", (entity, context) -> (double) entity.getAbsorptionAmount()),
        MAX_ABSORPTION("max_absorption", (entity, context) -> (double) entity.getMaxAbsorption()),
        ARMOR("armor", (entity, context) -> (double) entity.getArmorValue()),
        ARMOR_COVERAGE("armor_coverage", (entity, context) -> (double) entity.getArmorCoverPercentage()),
        AIR_SUPPLY("air_supply", (entity, context) -> (double) entity.getAirSupply()),
        MAX_AIR_SUPPLY("max_air_supply", (entity, context) -> (double) entity.getMaxAirSupply()),
        POSITION_X("pos_x", (entity, context) -> Mth.lerp(context.partialTick(), entity.xo, entity.getX())),
        POSITION_Y("pos_y", (entity, context) -> Mth.lerp(context.partialTick(), entity.yo, entity.getY())),
        POSITION_Z("pos_z", (entity, context) -> Mth.lerp(context.partialTick(), entity.zo, entity.getZ())),
        MOTION_X("motion_x", (entity, context) -> entity.getDeltaMovement().x),
        MOTION_Y("motion_y", (entity, context) -> entity.getDeltaMovement().y),
        MOTION_Z("motion_z", (entity, context) -> entity.getDeltaMovement().z),
        LIGHT_LEVEL("light_level", (entity, context) -> (double) entity.level().getLightEngine().getRawBrightness(entity.blockPosition(), 0));

        public static final Codec<Data> CODEC = StringRepresentable.fromEnum(Data::values);

        private final String name;
        private final BiFunction<LivingEntity, BackpackRenderContext, Double> function;

        Data(String name, BiFunction<LivingEntity, BackpackRenderContext, Double> function)
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
