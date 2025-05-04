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
        WALK_POSITION("walk_position", (entity, context) -> entity.walkAnimation.position(context.partialTick())),
        WALK_SPEED("walk_speed", (entity, context) -> entity.walkAnimation.speed(context.partialTick())),
        HEAD_YAW("head_yaw", (entity, context) -> entity.getViewXRot(context.partialTick())),
        HEAD_PITCH("head_pitch", (entity, context) -> entity.getViewYRot(context.partialTick())),
        BODY_ROTATION("body_rotation", (entity, context) -> Mth.rotLerp(context.partialTick(), entity.yBodyRotO, entity.yBodyRot)),
        SWIM_ANGLE("swimming", (entity, context) -> entity.getSwimAmount(context.partialTick())),
        ATTACK_SWING("attack_swing", (entity, context) -> entity.getAttackAnim(context.partialTick())),
        FALL_FLYING_COUNTER("fall_flying_counter", (entity, context) -> entity.isFallFlying() ? (float) entity.getFallFlyingTicks() + context.partialTick() : 0F),
        HEALTH("health", (entity, context) -> entity.getHealth()),
        MAX_HEALTH("max_health", (entity, context) -> entity.getMaxHealth()),
        ABSORPTION("absorption", (entity, context) -> entity.getAbsorptionAmount()),
        MAX_ABSORPTION("max_absorption", (entity, context) -> entity.getMaxAbsorption()),
        ARMOR("armor", (entity, context) -> (float) entity.getArmorValue()),
        ARMOR_COVERAGE("armor_coverage", (entity, context) -> entity.getArmorCoverPercentage()),
        AIR_SUPPLY("air_supply", (entity, context) -> (float) entity.getAirSupply()),
        MAX_AIR_SUPPLY("max_air_supply", (entity, context) -> (float) entity.getMaxAirSupply()),
        POSITION_X("pos_x", (entity, context) -> (float) Mth.lerp(context.partialTick(), entity.xo, entity.getX())),
        POSITION_Y("pos_y", (entity, context) -> (float) Mth.lerp(context.partialTick(), entity.yo, entity.getY())),
        POSITION_Z("pos_z", (entity, context) -> (float) Mth.lerp(context.partialTick(), entity.zo, entity.getZ())),
        MOTION_X("motion_x", (entity, context) -> (float) entity.getDeltaMovement().x),
        MOTION_Y("motion_y", (entity, context) -> (float) entity.getDeltaMovement().y),
        MOTION_Z("motion_z", (entity, context) -> (float) entity.getDeltaMovement().z);

        public static final Codec<Data> CODEC = StringRepresentable.fromEnum(Data::values);

        private final String name;
        private final BiFunction<LivingEntity, BackpackRenderContext, Float> function;

        Data(String name, BiFunction<LivingEntity, BackpackRenderContext, Float> function)
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
