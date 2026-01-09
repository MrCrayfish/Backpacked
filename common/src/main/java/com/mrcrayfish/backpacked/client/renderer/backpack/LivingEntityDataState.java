package com.mrcrayfish.backpacked.client.renderer.backpack;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public record LivingEntityDataState(
        double walkPosition,
        double walkSpeed,
        double headYaw,
        double headPitch,
        double bodyRotation,
        double swimAngle,
        double attackSwing,
        double fallFlyingCounter,
        double health,
        double maxHealth,
        double absorption,
        double maxAbsorption,
        double armor,
        double armorCoverage,
        double airSupply,
        double maxAirSupply,
        double posX,
        double posY,
        double posZ, 
        double motionX,
        double motionY,
        double motionZ,
        double lightLevel
)
{
    public static LivingEntityDataState create(LivingEntity entity, float partialTick)
    {
        double walkPosition = entity.walkAnimation.position(partialTick);
        double walkSpeed = entity.walkAnimation.speed(partialTick);
        double headYaw = entity.getViewXRot(partialTick);
        double headPitch = entity.getViewYRot(partialTick);
        double bodyRotation = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        double swimAngle = entity.getSwimAmount(partialTick);
        double attackSwing = entity.getAttackAnim(partialTick);
        double fallFlyingCounter = entity.isFallFlying() ? (double) entity.getFallFlyingTicks() + partialTick : 0;
        double health = entity.getHealth();
        double maxHealth = entity.getMaxHealth();
        double absorption = entity.getAbsorptionAmount();
        double maxAbsorption = entity.getMaxAbsorption();
        double armor = entity.getArmorValue();
        double armorCoverage = entity.getArmorCoverPercentage();
        double airSupply = entity.getAirSupply();
        double maxAirSupply = entity.getMaxAirSupply();
        double posX = Mth.lerp(partialTick, entity.xo, entity.getX());
        double posY = Mth.lerp(partialTick, entity.yo, entity.getY());
        double posZ = Mth.lerp(partialTick, entity.zo, entity.getZ());
        double motionX = entity.getDeltaMovement().x;
        double motionY = entity.getDeltaMovement().y;
        double motionZ = entity.getDeltaMovement().z;
        double lightLevel = entity.level().getLightEngine().getRawBrightness(entity.blockPosition(), 0);
        return new LivingEntityDataState(walkPosition, walkSpeed, headYaw, headPitch, bodyRotation, swimAngle, attackSwing, fallFlyingCounter, health, maxHealth, absorption, maxAbsorption, armor, armorCoverage, airSupply, maxAirSupply, posX, posY, posZ, motionX, motionY, motionZ, lightLevel);
    }
}
