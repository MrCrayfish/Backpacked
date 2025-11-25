package com.mrcrayfish.backpacked.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ShelfBlock extends HorizontalDirectionalBlock implements EntityBlock
{
    public static final MapCodec<ShelfBlock> CODEC = simpleCodec(ShelfBlock::new);

    private static final Map<Direction, VoxelShape> EMPTY_SHAPES = Maps.newEnumMap(ImmutableMap.of(
        Direction.NORTH, Block.box(2, 3, 7, 14, 5, 16),
        Direction.SOUTH, Block.box(2, 3, 0, 14, 5, 9),
        Direction.WEST, Block.box(7, 3, 2, 16, 5, 14),
        Direction.EAST, Block.box(0, 3, 2, 9, 5, 14))
    );

    private static final Map<Direction, VoxelShape> SHELVED_SHAPES = Maps.newEnumMap(ImmutableMap.of(
        Direction.NORTH, Block.box(2, 3, 7, 14, 15, 16),
        Direction.SOUTH, Block.box(2, 3, 0, 14, 15, 9),
        Direction.WEST, Block.box(7, 3, 2, 16, 15, 14),
        Direction.EAST, Block.box(0, 3, 2, 9, 15, 14))
    );

    public ShelfBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected MapCodec<ShelfBlock> codec()
    {
        return CODEC;
    }

    private boolean isInteractionTargetingShelf(BlockPos pos, BlockHitResult result)
    {
        Vec3 localHit = result.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        return localHit.y <= 0.3125;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(stack.getItem() instanceof BackpackItem)
        {
            if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf)
            {
                if(shelf.getBackpack().isEmpty())
                {
                    shelf.setBackpack(stack.copyAndClear());
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result)
    {
        if(!level.isClientSide())
        {
            if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf)
            {
                if(this.isInteractionTargetingShelf(pos, result))
                {
                    if(player instanceof ServerPlayer serverPlayer)
                    {
                        shelf.openShelfManagement(serverPlayer);
                    }
                }
                else if(!player.isCrouching() && !shelf.getBackpack().isEmpty())
                {
                    shelf.popBackpack(player);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
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
        if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf)
        {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(shelf.getContainer());
        }
        return 0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean unknown)
    {
        if(!state.is(newState.getBlock()))
        {
            if(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf)
            {
                if(level instanceof ServerLevel serverLevel)
                {
                    ((Recall.Access) serverLevel).backpacked$getRecall().onShelfBroken(shelf);
                }
                ItemStack stack = shelf.getBackpack();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copyAndClear());
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
        return Services.BACKPACK.createShelfBlockEntityType(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return level.isClientSide() ? ticker(type, ModBlockEntities.SHELF.get(), ShelfBlockEntity::clientTick) : null;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> ticker(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker)
    {
        //noinspection unchecked
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}
