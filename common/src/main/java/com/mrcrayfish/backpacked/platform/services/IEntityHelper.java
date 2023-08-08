package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;

/**
 * Author: MrCrayfish
 */
public interface IEntityHelper
{
    GoalSelector getGoalSelector(Mob mob);
}
