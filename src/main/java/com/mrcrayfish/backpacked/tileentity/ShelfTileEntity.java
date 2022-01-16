package com.mrcrayfish.backpacked.tileentity;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.core.ModTileEntities;
import com.mrcrayfish.backpacked.util.TileEntityUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ShelfTileEntity extends TileEntity
{
    private ItemStack backpack = ItemStack.EMPTY;

    public ShelfTileEntity(TileEntityType<?> type)
    {
        super(type);
    }

    public ShelfTileEntity()
    {
        super(ModTileEntities.SHELF.get());
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }

    public ActionResultType interact(PlayerEntity player)
    {
        ItemStack stack = Backpacked.getBackpackStack(player);
        ItemStack result = this.shelveBackpack(stack);
        Backpacked.setBackpackStack(player, result);
        return ActionResultType.SUCCESS;
    }

    public ItemStack shelveBackpack(ItemStack stack)
    {
        ItemStack shelvedBackpack = this.backpack.copy();
        this.backpack = stack.copy();
        if(!this.backpack.isEmpty() || !shelvedBackpack.isEmpty())
        {
            TileEntityUtil.sendUpdatePacket(this);
            this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS, 1.0F, 0.75F);
        }
        return shelvedBackpack;
    }

    public Direction getDirection()
    {
        return this.getBlockState().getValue(ShelfBlock.FACING);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag)
    {
        super.load(state, tag);
        this.backpack = ItemStack.of(tag.getCompound("Backpack"));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag)
    {
        tag.put("Backpack", this.backpack.save(new CompoundNBT()));
        return super.save(tag);
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.save(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, this.getUpdateTag());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.load(null, pkt.getTag());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return VoxelShapes.block().bounds().inflate(0.5).move(this.worldPosition);
    }
}
