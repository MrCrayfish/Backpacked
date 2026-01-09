package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.gui.pip.LivingEntityData;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
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
        LivingEntityData state = context.entityData();
        return state != null ? this.data.function.apply(state) : 0;
    }

    private enum Data implements StringRepresentable
    {
        WALK_POSITION("walk_position", LivingEntityData::walkPosition),
        WALK_SPEED("walk_speed", LivingEntityData::walkSpeed),
        HEAD_YAW("head_yaw", LivingEntityData::headYaw),
        HEAD_PITCH("head_pitch", LivingEntityData::headPitch),
        BODY_ROTATION("body_rotation", LivingEntityData::bodyRotation),
        SWIM_ANGLE("swimming", LivingEntityData::swimAngle),
        ATTACK_SWING("attack_swing", LivingEntityData::attackSwing),
        FALL_FLYING_COUNTER("fall_flying_counter", LivingEntityData::fallFlyingCounter),
        HEALTH("health", LivingEntityData::health),
        MAX_HEALTH("max_health", LivingEntityData::maxHealth),
        ABSORPTION("absorption", LivingEntityData::absorption),
        MAX_ABSORPTION("max_absorption", LivingEntityData::maxAbsorption),
        ARMOR("armor", LivingEntityData::armor),
        ARMOR_COVERAGE("armor_coverage", LivingEntityData::armorCoverage),
        AIR_SUPPLY("air_supply", LivingEntityData::airSupply),
        MAX_AIR_SUPPLY("max_air_supply", LivingEntityData::maxAirSupply),
        POSITION_X("pos_x", LivingEntityData::posX),
        POSITION_Y("pos_y", LivingEntityData::posY),
        POSITION_Z("pos_z", LivingEntityData::posZ),
        MOTION_X("motion_x", LivingEntityData::motionX),
        MOTION_Y("motion_y", LivingEntityData::motionY),
        MOTION_Z("motion_z", LivingEntityData::motionZ),
        LIGHT_LEVEL("light_level", LivingEntityData::lightLevel),
        ;

        public static final Codec<Data> CODEC = StringRepresentable.fromEnum(Data::values);

        private final String name;
        private final Function<LivingEntityData, Double> function;

        Data(String name, Function<LivingEntityData, Double> function)
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
