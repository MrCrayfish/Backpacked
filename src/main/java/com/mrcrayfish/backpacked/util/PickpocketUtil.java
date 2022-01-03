package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class PickpocketUtil
{
    public static AABB getBackpackBox(Player player, float partialTick)
    {
        AABB backpackBox = new AABB(-0.25, 0.0, -0.25, 0.25, 0.5625, 0.25);
        backpackBox = backpackBox.move(player.getPosition(partialTick));
        backpackBox = backpackBox.move(0, player.getPose() != Pose.SWIMMING ? 0.875 : 0.3125, 0);
        if(player.getPose() == Pose.CROUCHING)
        {
            backpackBox = backpackBox.move(0, -0.1875, 0);
        }
        float bodyRotation = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot);
        backpackBox = backpackBox.move(Vec3.directionFromRotation(0F, bodyRotation + 180F).scale(player.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return backpackBox;
    }

    public static boolean canPickpocketPlayer(Player targetPlayer, Player thiefPlayer)
    {
        return inRangeOfBackpack(targetPlayer, thiefPlayer) && inReachOfBackpack(targetPlayer, thiefPlayer);
    }

    public static boolean inRangeOfBackpack(Player targetPlayer, Player thiefPlayer)
    {
        if(targetPlayer.getPose() == Pose.SWIMMING) // Backpack is exposed at any direction
            return true;
        Vec3 between = thiefPlayer.getPosition(1.0F).subtract(targetPlayer.getPosition(1.0F));
        float angle = (float) Math.toDegrees(Math.atan2(between.z, between.x)) - 90F;
        float difference = Mth.degreesDifferenceAbs(targetPlayer.yBodyRot + 180F, angle);
        return difference <= Config.SERVER.pickpocketMaxRangeAngle.get();
    }

    public static boolean inReachOfBackpack(Player targetPlayer, Player thiefPlayer)
    {
        Vec3 pos = targetPlayer.getPosition(1.0F);
        pos = pos.add(Vec3.directionFromRotation(0F, targetPlayer.yBodyRot + 180F).scale(targetPlayer.getPose() != Pose.SWIMMING ? 0.3125 : -0.125));
        return pos.distanceTo(thiefPlayer.getPosition(1.0F)) <= Config.SERVER.pickpocketMaxReachDistance.get();
    }

    public static boolean canSeeBackpack(Player targetPlayer, Player thiefPlayer)
    {
        // Out of range for a valid reach and saves expensive computation
        if(targetPlayer.distanceTo(targetPlayer) > 4.0)
            return false;

        AABB backpackBox = getBackpackBox(targetPlayer, 1.0F);
        Vec3 start = thiefPlayer.getEyePosition(1.0F);
        Vec3 end = thiefPlayer.getViewVector(1.0F).scale(Config.SERVER.pickpocketMaxReachDistance.get() + 1.0).add(start);
        Optional<Vec3> hitPos = backpackBox.clip(start, end);
        if(!hitPos.isPresent())
            return false;

        BlockHitResult result = thiefPlayer.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, thiefPlayer));
        if(result.getType() == HitResult.Type.MISS)
            return true;

        return start.distanceTo(hitPos.get()) < start.distanceTo(result.getLocation());
    }
}
