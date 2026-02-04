package com.mrcrayfish.backpacked.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.blockentity.BackpackDockBlockEntity;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BackpackDockBlock extends HorizontalDirectionalBlock implements EntityBlock
{
    public static final MapCodec<BackpackDockBlock> CODEC = simpleCodec(BackpackDockBlock::new);

    public BackpackDockBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec()
    {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BackpackDockBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(stack.getItem() instanceof BackpackItem)
        {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof BackpackDockBlockEntity access)
            {
                if(this.isTargetingBackpackFrame(state, result))
                {
                    if(access.setBackpackOrPop(stack))
                    {
                        return ItemInteractionResult.SUCCESS;
                    }
                    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof BackpackDockBlockEntity access)
        {
            if(this.isTargetingBackpackFrame(state, result))
            {
                if(access.setBackpackOrPop(ItemStack.EMPTY))
                {
                    return InteractionResult.SUCCESS_NO_ITEM_USED;
                }
            }
            if(access.hasBackpack())
            {
                if(player instanceof ServerPlayer serverPlayer)
                {
                    access.openBackpackMenu(serverPlayer);
                }
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
            if(!level.isClientSide())
            {
                player.displayClientMessage(Component.translatable("backpacked.gui.missing_backpack"), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private boolean isTargetingBackpackFrame(BlockState state, BlockHitResult result)
    {
        Direction hitFace = result.getDirection();
        Direction blockDirection = state.getValue(FACING);
        if(hitFace != blockDirection.getOpposite()) // TODO test
            return false;

        BlockPos pos = result.getBlockPos();
        Vec3 local = result.getLocation().subtract(Vec3.atLowerCornerOf(pos));
        if(local.y < 0.1875 || local.y > 0.8125)
            return false;

        if(hitFace.getAxis() == Direction.Axis.X)
            return local.z >= 0.1875 &&  local.z <= 0.8125;

        if(hitFace.getAxis() == Direction.Axis.Z)
            return local.x >= 0.1875 &&  local.x <= 0.8125;

        return false;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean $$4)
    {
        if(!state.is(newState.getBlock()))
        {
            if(level.getBlockEntity(pos) instanceof BackpackDockBlockEntity entity)
            {
                ItemStack stack = entity.getBackpackWithContents();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
        super.onRemove(state, level, pos, newState, $$4);
    }
}
