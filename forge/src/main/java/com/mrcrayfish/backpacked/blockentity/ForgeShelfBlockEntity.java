package com.mrcrayfish.backpacked.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ForgeShelfBlockEntity extends ShelfBlockEntity
{
    public ForgeShelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    public ForgeShelfBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return Shapes.block().bounds().inflate(0.5).move(this.worldPosition);
    }
}
