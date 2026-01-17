package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.LivingEntityDataState;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.util.StringRepresentable;

import java.util.function.Function;

public record EntityDataValue(Data data) implements Value
{
    public static final Type TYPE = new Type(
        Utils.id("entity_data"),
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
        LivingEntityDataState state = context.entityData();
        return state != null ? this.data.function.apply(state) : 0;
    }

    private enum Data implements StringRepresentable
    {
        WALK_POSITION("walk_position", LivingEntityDataState::walkPosition),
        WALK_SPEED("walk_speed", LivingEntityDataState::walkSpeed),
        HEAD_YAW("head_yaw", LivingEntityDataState::headYaw),
        HEAD_PITCH("head_pitch", LivingEntityDataState::headPitch),
        BODY_ROTATION("body_rotation", LivingEntityDataState::bodyRotation),
        SWIM_ANGLE("swimming", LivingEntityDataState::swimAngle),
        ATTACK_SWING("attack_swing", LivingEntityDataState::attackSwing),
        FALL_FLYING_COUNTER("fall_flying_counter", LivingEntityDataState::fallFlyingCounter),
        HEALTH("health", LivingEntityDataState::health),
        MAX_HEALTH("max_health", LivingEntityDataState::maxHealth),
        ABSORPTION("absorption", LivingEntityDataState::absorption),
        MAX_ABSORPTION("max_absorption", LivingEntityDataState::maxAbsorption),
        ARMOR("armor", LivingEntityDataState::armor),
        ARMOR_COVERAGE("armor_coverage", LivingEntityDataState::armorCoverage),
        AIR_SUPPLY("air_supply", LivingEntityDataState::airSupply),
        MAX_AIR_SUPPLY("max_air_supply", LivingEntityDataState::maxAirSupply),
        POSITION_X("pos_x", LivingEntityDataState::posX),
        POSITION_Y("pos_y", LivingEntityDataState::posY),
        POSITION_Z("pos_z", LivingEntityDataState::posZ),
        MOTION_X("motion_x", LivingEntityDataState::motionX),
        MOTION_Y("motion_y", LivingEntityDataState::motionY),
        MOTION_Z("motion_z", LivingEntityDataState::motionZ),
        LIGHT_LEVEL("light_level", LivingEntityDataState::lightLevel),
        ;

        public static final Codec<Data> CODEC = StringRepresentable.fromEnum(Data::values);

        private final String name;
        private final Function<LivingEntityDataState, Double> function;

        Data(String name, Function<LivingEntityDataState, Double> function)
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
