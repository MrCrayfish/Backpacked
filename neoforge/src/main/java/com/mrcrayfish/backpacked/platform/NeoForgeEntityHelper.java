package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IEntityHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;

/**
 * Author: MrCrayfish
 */
public class NeoForgeEntityHelper implements IEntityHelper
{
    @Override
    public GoalSelector getGoalSelector(Mob mob)
    {
        return mob.goalSelector;
    }
}
