package com.mrcrayfish.backpacked.tileentity;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ShelfBlockEntity extends BlockEntity
{
    private ItemStack backpack = ItemStack.EMPTY;

    public ShelfBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public ShelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.SHELF.get(), pos, state);
    }

    public InteractionResult interact(Player player)
    {
        ItemStack stack = Backpacked.getBackpackStack(player);
        ItemStack result = this.shelveBackpack(stack);
        Backpacked.setBackpackStack(player, result);
        return InteractionResult.SUCCESS;
    }

    public ItemStack shelveBackpack(ItemStack stack)
    {
        ItemStack shelvedBackpack = this.backpack.copy();
        this.backpack = stack.copy();
        if(!this.backpack.isEmpty() || !shelvedBackpack.isEmpty())
        {
            BlockEntityUtil.sendUpdatePacket(this);
        }
        return shelvedBackpack;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.backpack = ItemStack.of(tag.getCompound("Backpack"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        tag.put("Backpack", this.backpack.save(new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithFullMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        super.onDataPacket(net, pkt);
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }
}
