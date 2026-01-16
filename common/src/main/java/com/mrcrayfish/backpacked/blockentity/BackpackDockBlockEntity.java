package com.mrcrayfish.backpacked.blockentity;

import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.BackpackState;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.UnlockableContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.BlockEntityUtil;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BackpackDockBlockEntity extends BlockEntity implements IOptionalStorage
{
    private ItemStack backpack = ItemStack.EMPTY;
    private @Nullable ItemStackContainer inventory;

    public BackpackDockBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.BACKPACK_ACCESS.get(), pos, state);
    }

    @Override
    @Nullable
    public Container getInventory()
    {
        return this.inventory;
    }

    public boolean hasBackpack()
    {
        return !this.backpack.isEmpty();
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }

    public boolean setBackpackOrPop(ItemStack backpack)
    {
        if(this.popBackpack())
            return true;

        if(backpack.getItem() instanceof BackpackItem)
        {
            if(this.level instanceof ServerLevel)
            {
                this.backpack = backpack.copy();
                this.updateInventory();
                this.setChanged();
                backpack.setCount(0);
                BlockEntityUtil.sendUpdatePacket(this);
                this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
            }
            return true;
        }
        return false;
    }

    private boolean popBackpack()
    {
        if(!this.backpack.isEmpty())
        {
            if(this.level instanceof ServerLevel)
            {
                ItemStack stack = this.getBackpackWithContents();
                this.backpack = ItemStack.EMPTY;
                this.inventory = null;
                this.setChanged();
                Vec3 spawn = this.getPopPosition();
                ItemEntity entity = new ItemEntity(this.level, spawn.x, spawn.y, spawn.z, stack);
                entity.setDefaultPickUpDelay();
                this.level.addFreshEntity(entity);
                BlockEntityUtil.sendUpdatePacket(this);
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
            }
            return true;
        }
        return false;
    }

    private void playSound(SoundEvent event)
    {
        float pitch = this.level.random.nextFloat() * 0.2F + 0.9F;
        this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, pitch, 1.0F);
    }

    private Vec3 getPopPosition()
    {
        return this.getBlockPos().getCenter().relative(this.getDirection(), 0.6875);
    }

    private ItemStack getBackpackWithContents()
    {
        ItemStack stack = this.backpack;
        if(!stack.isEmpty())
        {
            this.copyInventoryToStack(stack);
        }
        return stack.copy();
    }

    private void copyInventoryToStack(ItemStack stack)
    {
        if(!stack.isEmpty() && this.inventory != null)
        {
            stack.set(DataComponents.CONTAINER, this.inventory.createContents());
        }
    }

    public Direction getDirection()
    {
        BlockState state = this.getBlockState();
        if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
        {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        }
        return Direction.NORTH;
    }

    private void updateInventory()
    {
        if(this.level instanceof ServerLevel)
        {
            ItemStack stack = this.backpack;
            if(stack.getItem() instanceof BackpackItem)
            {
                if(this.inventory == null || stack != this.inventory.stack)
                {
                    ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                    this.inventory = new ItemStackContainer(this, this.getBackpackSize());
                    this.inventory.copyFrom(contents);
                    this.setChanged();
                    return;
                }
                this.resizeInventory();
                return;
            }
            this.inventory = null;
            this.setChanged();
        }
    }

    private void resizeInventory()
    {
        int backpackSize = this.getBackpackSize();
        if(this.inventory != null && this.inventory.getState().isInvalid())
        {
            Container oldInventory = this.inventory;
            this.inventory = new ItemStackContainer(this, backpackSize);
            InventoryHelper.mergeInventoryOrSpawnIntoLevel(oldInventory, this.inventory, this.level, this.getPopPosition());
            this.setChanged();
        }
    }

    private int getBackpackSize()
    {
        if(this.backpack.getItem() instanceof BackpackItem item)
        {
            return item.getRowCount() * item.getColumnCount();
        }
        return 0;
    }

    public void openBackpackMenu(ServerPlayer player)
    {
        this.updateInventory();
        if(this.inventory != null && this.inventory.stillValid(player) && this.backpack.getItem() instanceof BackpackItem item)
        {
            Component title = this.backpack.getHoverName();
            int cols = item.getColumnCount();
            int rows = item.getRowCount();
            UnlockableSlots slots = item.getUnlockableSlots(this.backpack);
            if(slots != null)
            {
                Services.BACKPACK.openBackpackScreen(player, this.inventory, -1, -1, cols, rows, false, slots, Pagination.NONE, Augments.EMPTY, title, UnlockableSlots.NONE);
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.backpack = input.read("Backpack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.updateInventory();
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.store("Backpack", ItemStack.OPTIONAL_CODEC, this.backpack);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        return this.saveCustomOnly(provider);
    }

    @Override
    public void saveCustomOnly(ValueOutput output)
    {
        output.store("Backpack", ItemStack.OPTIONAL_CODEC, this.backpack);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static class ItemStackContainer extends UnlockableContainer
    {
        private final BackpackDockBlockEntity entity;
        private final ItemStack stack;
        private final BackpackState state;

        public ItemStackContainer(BackpackDockBlockEntity entity, int size)
        {
            super(size);
            this.entity = entity;
            this.stack = this.entity.backpack;
            this.state = BackpackState.create(this.stack);
        }

        @Override
        protected UnlockableSlots getUnlockableSlots()
        {
            if(this.stack.getItem() instanceof BackpackItem item)
            {
                return item.getUnlockableSlots(this.stack);
            }
            return UnlockableSlots.ALL;
        }

        @Override
        public void setChanged()
        {
            this.entity.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack)
        {
            return BackpackInventory.isAllowedItem(stack) && super.canPlaceItem(slot, stack);
        }

        @Override
        public boolean stillValid(Player player)
        {
            return this.entity.inventory == this && this.stack == this.entity.backpack && this.stack.getItem() instanceof BackpackItem && !this.entity.isRemoved();
        }

        public ItemContainerContents createContents()
        {
            return ItemContainerContents.fromItems(this.items);
        }

        public ItemStack getBackpackStack()
        {
            return this.stack;
        }

        public BackpackDockBlockEntity getBlockEntity()
        {
            return this.entity;
        }

        public BackpackState getState()
        {
            return this.state;
        }
    }
}
