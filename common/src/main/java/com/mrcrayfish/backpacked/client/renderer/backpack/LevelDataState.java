package com.mrcrayfish.backpacked.client.renderer.backpack;

import net.minecraft.world.level.Level;

public record LevelDataState(double timeOfDay, double gameTime, double rainLevels, double thunderLevels, double seaLevel, double maxLightLevel)
{
    public static LevelDataState create(Level level, float partialTick)
    {
        double timeOfDay = level.getOverworldClockTime();
        double gameTime = level.getLevelData().getGameTime();
        double rainLevels = level.getRainLevel(partialTick);
        double thunderLevels = level.getThunderLevel(partialTick);
        double seaLevel = level.getSeaLevel();
        double maxLightLevel = 15;
        return new LevelDataState(timeOfDay, gameTime, rainLevels, thunderLevels, seaLevel, maxLightLevel);
    }
}
