package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class PickpocketUtil
{
    public static Vector3d getEntityPos(Entity entity, float partialTick)
    {
        double x = MathHelper.lerp(partialTick, entity.xo, entity.getX());
        double y = MathHelper.lerp(partialTick, entity.yo, entity.getY());
        double z = MathHelper.lerp(partialTick, entity.zo, entity.getZ());
        return new Vector3d(x, y, z);
    }

    public static AxisAlignedBB getBackpackBox(LivingEntity entity, float partialTick)
    {
        AxisAlignedBB backpackBox = new AxisAlignedBB(-0.25, 0.0, -0.25, 0.25, 0.5625, 0.25);
        backpackBox = backpackBox.move(getEntityPos(entity, partialTick));
        backpackBox = backpackBox.move(0, entity.getPose() != Pose.SWIMMING ? 0.875 : 0.3125, 0);
        if(entity.getPose() == Pose.CROUCHING)
        {
            backpackBox = backpackBox.move(0, -0.1875, 0);
        }
        float bodyRotation = MathHelper.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        backpackBox = backpackBox.move(Vector3d.directionFromRotation(0F, bodyRotation + 180F).scale(entity.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return backpackBox;
    }

    public static boolean canPickpocketEntity(LivingEntity targetEntity, PlayerEntity thiefPlayer)
    {
        return canPickpocketEntity(targetEntity, thiefPlayer, Config.SERVER.pickpocketMaxReachDistance.get());
    }

    public static boolean canPickpocketEntity(LivingEntity targetEntity, PlayerEntity thiefPlayer, double range)
    {
        return inRangeOfBackpack(targetEntity, thiefPlayer) && inReachOfBackpack(targetEntity, thiefPlayer, range);
    }

    public static boolean inRangeOfBackpack(LivingEntity targetEntity, PlayerEntity thiefPlayer)
    {
        if(targetEntity.getPose() == Pose.SWIMMING) // Backpack is exposed at any direction
            return true;
        Vector3d between = getEntityPos(thiefPlayer, 1.0F).subtract(getEntityPos(targetEntity, 1.0F));
        float angle = (float) Math.toDegrees(Math.atan2(between.z, between.x)) - 90F;
        float difference = MathHelper.degreesDifferenceAbs(targetEntity.yBodyRot + 180F, angle);
        return difference <= Config.SERVER.pickpocketMaxRangeAngle.get();
    }

    public static boolean inReachOfBackpack(LivingEntity targetPlayer, PlayerEntity thiefPlayer, double reachDistance)
    {
        Vector3d pos = getEntityPos(targetPlayer, 1.0F);
        pos = pos.add(Vector3d.directionFromRotation(0F, targetPlayer.yBodyRot + 180F).scale(targetPlayer.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return pos.distanceTo(getEntityPos(thiefPlayer, 1.0F)) <= reachDistance;
    }

    public static boolean canSeeBackpack(LivingEntity targetEntity, PlayerEntity thiefPlayer)
    {
        // Out of range for a valid reach and saves expensive computation
        if(targetEntity.distanceTo(targetEntity) > 4.0)
            return false;

        AxisAlignedBB backpackBox = getBackpackBox(targetEntity, 1.0F);
        Vector3d start = thiefPlayer.getEyePosition(1.0F);
        Vector3d end = thiefPlayer.getViewVector(1.0F).scale(Config.SERVER.pickpocketMaxReachDistance.get()).add(start);
        Optional<Vector3d> hitPos = backpackBox.clip(start, end);
        if(!hitPos.isPresent())
            return false;

        BlockRayTraceResult result = thiefPlayer.level.clip(new RayTraceContext(start, hitPos.get(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, thiefPlayer));
        if(result.getType() == RayTraceResult.Type.MISS)
            return true;

        return start.distanceTo(hitPos.get()) < start.distanceTo(result.getLocation());
    }
}
