package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class PickpocketUtil
{
    public static Vec3 getEntityPos(Entity entity, float partialTick)
    {
        double x = Mth.lerp(partialTick, entity.xo, entity.getX());
        double y = Mth.lerp(partialTick, entity.yo, entity.getY());
        double z = Mth.lerp(partialTick, entity.zo, entity.getZ());
        return new Vec3(x, y, z);
    }

    public static AABB getBackpackBox(LivingEntity entity, float partialTick)
    {
        AABB backpackBox = new AABB(-0.25, 0.0, -0.25, 0.25, 0.5625, 0.25);
        backpackBox = backpackBox.move(getEntityPos(entity, partialTick));
        backpackBox = backpackBox.move(0, entity.getPose() != Pose.SWIMMING ? 0.875 : 0.3125, 0);
        if(entity.getPose() == Pose.CROUCHING)
        {
            backpackBox = backpackBox.move(0, -0.1875, 0);
        }
        float bodyRotation = Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        backpackBox = backpackBox.move(Vec3.directionFromRotation(0F, bodyRotation + 180F).scale(entity.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return backpackBox;
    }

    public static boolean canPickpocketEntity(LivingEntity targetEntity, Player thiefPlayer)
    {
        return canPickpocketEntity(targetEntity, thiefPlayer, Config.SERVER.pickpocketMaxReachDistance.get());
    }

    public static boolean canPickpocketEntity(LivingEntity targetEntity, Player thiefPlayer, double range)
    {
        return inRangeOfBackpack(targetEntity, thiefPlayer) && inReachOfBackpack(targetEntity, thiefPlayer, range);
    }

    public static boolean inRangeOfBackpack(LivingEntity livingEntity, Player thiefPlayer)
    {
        if(livingEntity.getPose() == Pose.SWIMMING) // Backpack is exposed at any direction
            return true;
        Vec3 between = getEntityPos(thiefPlayer, 1.0F).subtract(getEntityPos(livingEntity, 1.0F));
        float angle = (float) Math.toDegrees(Math.atan2(between.z, between.x)) - 90F;
        float difference = Mth.degreesDifferenceAbs(livingEntity.yBodyRot + 180F, angle);
        return difference <= Config.SERVER.pickpocketMaxRangeAngle.get();
    }

    public static boolean inReachOfBackpack(LivingEntity targetPlayer, Player thiefPlayer, double reachDistance)
    {
        Vec3 pos = getEntityPos(targetPlayer, 1.0F);
        pos = pos.add(Vec3.directionFromRotation(0F, targetPlayer.yBodyRot + 180F).scale(targetPlayer.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return pos.distanceTo(getEntityPos(thiefPlayer, 1.0F)) <= reachDistance;
    }

    public static boolean canSeeBackpack(LivingEntity targetEntity, Player thiefPlayer)
    {
        // Out of range for a valid reach and saves expensive computation
        if(targetEntity.distanceTo(thiefPlayer) > 4.0)
            return false;

        AABB backpackBox = getBackpackBox(targetEntity, 1.0F);
        Vec3 start = thiefPlayer.getEyePosition(1.0F);
        Vec3 end = thiefPlayer.getViewVector(1.0F).scale(Config.SERVER.pickpocketMaxReachDistance.get()).add(start);
        Optional<Vec3> hitPos = backpackBox.clip(start, end);
        if(!hitPos.isPresent())
            return false;

        BlockHitResult result = thiefPlayer.level.clip(new ClipContext(start, hitPos.get(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, thiefPlayer));
        if(result.getType() == HitResult.Type.MISS)
            return true;

        return start.distanceTo(hitPos.get()) < start.distanceTo(result.getLocation());
    }
}
