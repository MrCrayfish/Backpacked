package com.mrcrayfish.backpacked.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.tileentity.ShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ShelfBlock extends HorizontalDirectionalBlock implements EntityBlock
{
    private static final Map<Direction, VoxelShape> EMPTY_SHAPES = Maps.newEnumMap(ImmutableMap.of(
        Direction.NORTH, Block.box(2, 3, 7, 14, 5, 16),
        Direction.SOUTH, Block.box(2, 3, 0, 14, 5, 9),
        Direction.WEST, Block.box(7, 3, 2, 16, 5, 14),
        Direction.EAST, Block.box(0, 3, 2, 9, 5, 14))
    );

    private static final Map<Direction, VoxelShape> SHELVED_SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.box(2, 3, 7, 14, 14, 16),
            Direction.SOUTH, Block.box(2, 3, 0, 14, 14, 9),
            Direction.WEST, Block.box(7, 3, 2, 16, 14, 14),
            Direction.EAST, Block.box(0, 3, 2, 9, 14, 14))
    );

    public ShelfBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide())
        {
            if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelfBlockEntity)
            {
                return shelfBlockEntity.interact(player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        if(getter.getBlockEntity(pos) instanceof ShelfBlockEntity shelfBlockEntity)
        {
            if(!shelfBlockEntity.getBackpack().isEmpty())
            {
                return SHELVED_SHAPES.get(state.getValue(FACING));
            }
        }
        return EMPTY_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
    {
        Direction facing = state.getValue(FACING);
        BlockPos relativePos = pos.relative(facing.getOpposite());
        BlockState relativeState = reader.getBlockState(relativePos);
        return relativeState.isFaceSturdy(reader, relativePos, facing);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction clickedFace = context.getClickedFace();
        if(clickedFace.getAxis().isHorizontal())
        {
            BlockState potentialState = this.defaultBlockState().setValue(FACING, clickedFace);
            if(this.canSurvive(potentialState, context.getLevel(), context.getClickedPos()))
            {
                return potentialState;
            }
        }
        return null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelfBlockEntity)
        {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(shelfBlockEntity);
        }
        return 0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean unknown)
    {
        if(!state.is(newState.getBlock()))
        {
            if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelfBlockEntity)
            {
                boolean dropsContents = Config.SERVER.dropContentsFromShelf.get();
                ItemStack stack = dropsContents ? shelfBlockEntity.getBackpack() : shelfBlockEntity.getBackpackWithContents();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                if(dropsContents) Containers.dropContents(level, pos, shelfBlockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, unknown);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new ShelfBlockEntity(pos, state);
    }
}
