package com.mrcrayfish.backpacked.tileentity;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModSounds;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.util.BlockEntityUtil;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ShelfBlockEntity extends BlockEntity implements IOptionalStorage
{
    private ItemStack backpack = ItemStack.EMPTY;
    private SimpleContainer inventory = null;
    private LazyOptional<IItemHandlerModifiable> itemHandler;

    public ShelfBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public ShelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.SHELF.get(), pos, state);
    }

    @Override
    @Nullable
    public SimpleContainer getInventory()
    {
        return this.inventory;
    }

    public ItemStack getBackpackWithContents()
    {
        ItemStack stack = this.backpack.copy();
        if(!stack.isEmpty())
        {
            this.copyInventoryToStack(stack);
        }
        return stack;
    }

    public InteractionResult interact(Player player)
    {
        if(player.isCrouching() || this.backpack.isEmpty())
        {
            ItemStack stack = Backpacked.getBackStack(player);
            if(!stack.isEmpty() && !(stack.getItem() instanceof BackpackItem))
            {
                if(!this.backpack.isEmpty())
                {
                    player.displayClientMessage(new TranslatableComponent("message.backpacked.occupied_back_slot"), true);
                }
                return InteractionResult.FAIL;
            }
            ItemStack result = this.shelveBackpack(stack);
            Backpacked.setBackpackStack(player, result);
        }
        else if(player instanceof ServerPlayer serverPlayer)
        {
            this.openBackpackInventory(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    public ItemStack shelveBackpack(ItemStack stack)
    {
        ItemStack shelvedBackpack = this.backpack.copy();
        this.copyInventoryToStack(shelvedBackpack);
        this.backpack = stack.copy();
        if(!this.backpack.isEmpty() || !shelvedBackpack.isEmpty())
        {
            boolean removed = this.backpack.isEmpty();
            this.updateInventory(false);
            BlockEntityUtil.sendUpdatePacket(this);
            this.level.playSound(null, this.worldPosition, ModSounds.ITEM_BACKPACK_PLACE.get(), SoundSource.BLOCKS, 1.0F, removed ? 0.75F : 1.0F);
            this.setChanged();
        }
        return shelvedBackpack;
    }

    private void openBackpackInventory(ServerPlayer player)
    {
        this.getBackpackInventory().ifPresent(inventory ->
        {
            this.getBackpackItem().ifPresent(backpackItem ->
            {
                Component title = this.backpack.hasCustomHoverName() ? this.backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
                int cols = backpackItem.getColumnCount();
                int rows = backpackItem.getRowCount();
                NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, entity) -> {
                    return new BackpackContainerMenu(id, playerInventory, inventory, cols, rows, false);
                }, title), buffer -> {
                    buffer.writeVarInt(cols);
                    buffer.writeVarInt(rows);
                    buffer.writeBoolean(false);
                });
            });
        });
    }

    private void copyInventoryToStack(ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            this.getBackpackInventory().ifPresent(inventory ->
            {
                stack.getOrCreateTag().put("Items", InventoryHelper.saveAllItems(new ListTag(), inventory));
            });
        }
    }

    private Optional<SimpleContainer> getBackpackInventory()
    {
        if(this.backpack.isEmpty())
        {
            return Optional.empty();
        }
        if(this.inventory != null && this.inventory.getContainerSize() != this.getBackpackSize())
        {
            this.updateInventory(true);
        }
        return Optional.ofNullable(this.inventory);
    }

    private void updateInventory(boolean resized)
    {
        if(!this.backpack.isEmpty())
        {
            SimpleContainer oldInventory = this.inventory;
            this.inventory = new ShelfContainer(this.getBackpackSize());
            CompoundTag compound = this.backpack.getOrCreateTag();
            this.loadBackpackItems(compound);
            compound.remove("Items");
            if(resized && oldInventory != null)
            {
                InventoryHelper.mergeInventory(oldInventory, this.inventory, this.level, Vec3.atCenterOf(this.worldPosition));
            }
        }
        else
        {
            this.inventory = null;
        }
    }

    private void loadBackpackItems(CompoundTag compound)
    {
        if(compound.contains("Items", Tag.TAG_LIST))
        {
            InventoryHelper.loadAllItems(compound.getList("Items", Tag.TAG_COMPOUND), this.inventory, this.level, Vec3.atCenterOf(this.worldPosition));
        }
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.backpack = ItemStack.of(tag.getCompound("Backpack"));
        this.inventory = this.backpack.isEmpty() ? null : new ShelfContainer(this.getBackpackSize());
        this.loadBackpackItems(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        tag.put("Backpack", this.backpack.save(new CompoundTag()));
        if(this.inventory != null)
        {
            ListTag items = new ListTag();
            InventoryHelper.saveAllItems(items, this.inventory);
            tag.put("Items", items);
        }
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

    public Direction getDirection()
    {
        return this.getBlockState().getValue(ShelfBlock.FACING);
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return Shapes.block().bounds().inflate(0.5).move(this.worldPosition);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        if(this.itemHandler != null)
        {
            LazyOptional<?> oldHandler = this.itemHandler;
            this.itemHandler = null;
            oldHandler.invalidate();
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if(!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if(this.itemHandler == null)
            {
                this.itemHandler = LazyOptional.of(() -> new InvWrapper(this));
            }
            return this.itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private Optional<BackpackItem> getBackpackItem()
    {
        if(this.backpack.getItem() instanceof BackpackItem)
        {
            return Optional.of((BackpackItem) this.backpack.getItem());
        }
        return Optional.empty();
    }

    private int getBackpackSize()
    {
        return this.getBackpackItem().map(item -> item.getRowCount() * item.getColumnCount()).orElse(0);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return !BackpackSlot.isBannedItem(stack);
    }

    // Need this to call set changed
    public class ShelfContainer extends SimpleContainer
    {
        public ShelfContainer(int size)
        {
            super(size);
        }

        @Override
        public void setChanged()
        {
            super.setChanged();
            ShelfBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player)
        {
            return ShelfBlockEntity.this.inventory == this && !ShelfBlockEntity.this.backpack.isEmpty() && !ShelfBlockEntity.this.remove;
        }
    }
}
