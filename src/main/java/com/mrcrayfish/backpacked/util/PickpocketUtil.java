package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Config;
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
    private static Vector3d getEntityPos(PlayerEntity player, float partialTick)
    {
        double x = MathHelper.lerp(partialTick, player.xo, player.getX());
        double y = MathHelper.lerp(partialTick, player.yo, player.getY());
        double z = MathHelper.lerp(partialTick, player.zo, player.getZ());
        return new Vector3d(x, y, z);
    }

    public static AxisAlignedBB getBackpackBox(PlayerEntity player, float partialTick)
    {
        AxisAlignedBB backpackBox = new AxisAlignedBB(-0.25, 0.0, -0.25, 0.25, 0.5625, 0.25);
        backpackBox = backpackBox.move(getEntityPos(player, partialTick));
        backpackBox = backpackBox.move(0, player.getPose() != Pose.SWIMMING ? 0.875 : 0.3125, 0);
        if(player.getPose() == Pose.CROUCHING)
        {
            backpackBox = backpackBox.move(0, -0.1875, 0);
        }
        float bodyRotation = MathHelper.lerp(partialTick, player.yBodyRotO, player.yBodyRot);
        backpackBox = backpackBox.move(Vector3d.directionFromRotation(0F, bodyRotation + 180F).scale(player.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return backpackBox;
    }

    public static boolean canPickpocketPlayer(PlayerEntity targetPlayer, PlayerEntity thiefPlayer)
    {
        return inRangeOfBackpack(targetPlayer, thiefPlayer) && inReachOfBackpack(targetPlayer, thiefPlayer);
    }

    public static boolean inRangeOfBackpack(PlayerEntity targetPlayer, PlayerEntity thiefPlayer)
    {
        if(targetPlayer.getPose() == Pose.SWIMMING) // Backpack is exposed at any direction
            return true;
        Vector3d between = getEntityPos(thiefPlayer, 1.0F).subtract(getEntityPos(targetPlayer, 1.0F));
        float angle = (float) Math.toDegrees(Math.atan2(between.z, between.x)) - 90F;
        float difference = MathHelper.degreesDifferenceAbs(targetPlayer.yBodyRot + 180F, angle);
        return difference <= Config.SERVER.pickpocketMaxRangeAngle.get();
    }

    public static boolean inReachOfBackpack(PlayerEntity targetPlayer, PlayerEntity thiefPlayer)
    {
        Vector3d pos = getEntityPos(targetPlayer, 1.0F);
        pos = pos.add(Vector3d.directionFromRotation(0F, targetPlayer.yBodyRot + 180F).scale(targetPlayer.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return pos.distanceTo(getEntityPos(thiefPlayer, 1.0F)) <= Config.SERVER.pickpocketMaxReachDistance.get();
    }

    public static boolean canSeeBackpack(PlayerEntity targetPlayer, PlayerEntity thiefPlayer)
    {
        // Out of range for a valid reach and saves expensive computation
        if(targetPlayer.distanceTo(targetPlayer) > 4.0)
            return false;

        AxisAlignedBB backpackBox = getBackpackBox(targetPlayer, 1.0F);
        Vector3d start = thiefPlayer.getEyePosition(1.0F);
        Vector3d end = thiefPlayer.getViewVector(1.0F).scale(Config.SERVER.pickpocketMaxReachDistance.get() + 1.0).add(start);
        Optional<Vector3d> hitPos = backpackBox.clip(start, end);
        if(!hitPos.isPresent())
            return false;

        BlockRayTraceResult result = thiefPlayer.level.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, thiefPlayer));
        if(result.getType() == RayTraceResult.Type.MISS)
            return true;

        return start.distanceTo(hitPos.get()) < start.distanceTo(result.getLocation());
    }
}
