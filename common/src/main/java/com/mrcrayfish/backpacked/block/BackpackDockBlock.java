package com.mrcrayfish.backpacked.block;

import com.mrcrayfish.backpacked.blockentity.BackpackDockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public BackpackDockBlock(Properties properties)
    {
        super(properties);
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        ItemStack stack = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof BackpackDockBlockEntity access)
        {
            if(this.isTargetingBackpackFrame(state, result))
            {
                if(access.setBackpackOrPop(stack))
                {
                    return InteractionResult.SUCCESS;
                }
            }
            if(access.hasBackpack())
            {
                if(player instanceof ServerPlayer serverPlayer)
                {
                    access.openBackpackMenu(serverPlayer);
                }
                return InteractionResult.SUCCESS;
            }
            if(!level.isClientSide())
            {
                player.displayClientMessage(Component.translatable("backpacked.gui.missing_backpack"), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private boolean isTargetingBackpackFrame(BlockState state, BlockHitResult result)
    {
        Direction hitFace = result.getDirection();
        Direction blockDirection = state.getValue(FACING);
        if(hitFace != blockDirection.getOpposite())
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
}
