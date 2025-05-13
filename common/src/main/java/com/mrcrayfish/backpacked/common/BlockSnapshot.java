package com.mrcrayfish.backpacked.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record BlockSnapshot(ServerLevel level, BlockState state, BlockPos pos, @Nullable CompoundTag tag, Holder<Biome> biome, int timeOfDay)
{
    public static BlockSnapshot capture(ServerLevel level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        Holder<Biome> biome = level.getBiome(pos);
        int timeOfDay = (int) level.getTimeOfDay(0);
        return new BlockSnapshot(level, state, pos, null, biome, timeOfDay);
    }

    public static BlockSnapshot captureWithTag(ServerLevel level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        Holder<Biome> biome = level.getBiome(pos);
        CompoundTag tag = captureBlockEntityTag(level, pos);
        int timeOfDay = (int) level.getTimeOfDay(0);
        return new BlockSnapshot(level, state, pos, tag, biome, timeOfDay);
    }

    @Nullable
    private static CompoundTag captureBlockEntityTag(ServerLevel level, BlockPos pos)
    {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity != null)
        {
            return entity.saveWithFullMetadata(level.registryAccess());
        }
        return null;
    }
}
