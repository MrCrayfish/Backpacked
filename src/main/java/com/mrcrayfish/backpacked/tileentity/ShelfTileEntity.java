package com.mrcrayfish.backpacked.tileentity;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.core.ModTileEntities;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.backpacked.util.TileEntityUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ShelfTileEntity extends TileEntity implements IOptionalStorage
{
    private ItemStack backpack = ItemStack.EMPTY;
    private Inventory inventory = null;
    private LazyOptional<IItemHandlerModifiable> itemHandler;

    public ShelfTileEntity(TileEntityType<?> type)
    {
        super(type);
    }

    public ShelfTileEntity()
    {
        super(ModTileEntities.SHELF.get());
    }

    @Nullable
    @Override
    public Inventory getInventory()
    {
        return this.inventory;
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }

    public ActionResultType interact(PlayerEntity player)
    {
        if(player.isCrouching() || this.backpack.isEmpty())
        {
            ItemStack stack = Backpacked.getBackpackStack(player);
            ItemStack result = this.shelveBackpack(stack);
            Backpacked.setBackpackStack(player, result);
        }
        else if(player instanceof ServerPlayerEntity)
        {
            this.openBackpackInventory((ServerPlayerEntity) player);
        }
        return ActionResultType.SUCCESS;
    }

    public ItemStack shelveBackpack(ItemStack stack)
    {
        ItemStack shelvedBackpack = this.backpack.copy();
        this.copyInventoryToStack(shelvedBackpack);
        this.backpack = stack.copy();
        if(!this.backpack.isEmpty() || !shelvedBackpack.isEmpty())
        {
            this.updateInventory();
            TileEntityUtil.sendUpdatePacket(this);
            this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS, 1.0F, 0.75F);
            this.setChanged();
        }
        return shelvedBackpack;
    }

    private void openBackpackInventory(ServerPlayerEntity player)
    {
        this.getBackpackInventory().ifPresent(inventory ->
        {
            this.getBackpackItem().ifPresent(backpackItem ->
            {
                ITextComponent title = this.backpack.hasCustomHoverName() ? this.backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
                int cols = backpackItem.getColumnCount();
                int rows = backpackItem.getRowCount();
                NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, entity) -> {
                    return new BackpackContainer(id, playerInventory, inventory, cols, rows, false);
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
                stack.getOrCreateTag().put("Items", InventoryHelper.saveAllItems(new ListNBT(), inventory));
            });
        }
    }

    private Optional<Inventory> getBackpackInventory()
    {
        if(this.backpack.isEmpty())
        {
            return Optional.empty();
        }
        if(this.inventory != null && this.inventory.getContainerSize() != getBackpackSize())
        {
            this.updateInventory();
        }
        return Optional.ofNullable(this.inventory);
    }

    private void updateInventory()
    {
        if(!this.backpack.isEmpty())
        {
            Inventory oldInventory = this.inventory;
            this.inventory = new ShelfInventory(getBackpackSize());
            CompoundNBT compound = this.backpack.getOrCreateTag();
            this.loadBackpackItems(compound);
            compound.remove("Items");
            if(oldInventory != null)
            {
                InventoryHelper.mergeInventory(oldInventory, this.inventory, this.level, Vector3d.atCenterOf(this.worldPosition));
            }
        }
        else
        {
            this.inventory = null;
        }
    }

    private void loadBackpackItems(CompoundNBT compound)
    {
        if(compound.contains("Items", Constants.NBT.TAG_LIST))
        {
            InventoryHelper.loadAllItems(compound.getList("Items", Constants.NBT.TAG_COMPOUND), this.inventory, this.level, Vector3d.atCenterOf(this.worldPosition));
        }
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
        this.inventory = this.backpack.isEmpty() ? null : new ShelfInventory(getBackpackSize());
        this.loadBackpackItems(tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag)
    {
        tag.put("Backpack", this.backpack.save(new CompoundNBT()));
        if(this.inventory != null)
        {
            ListNBT items = new ListNBT();
            InventoryHelper.saveAllItems(items, this.inventory);
            tag.put("Items", items);
        }
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
    public void clearCache()
    {
        super.clearCache();
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

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return VoxelShapes.block().bounds().inflate(0.5).move(this.worldPosition);
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

    // Need this to call set changed
    public class ShelfInventory extends Inventory
    {
        public ShelfInventory(int size)
        {
            super(size);
        }

        @Override
        public void setChanged()
        {
            super.setChanged();
            ShelfTileEntity.this.setChanged();
        }
    }
}
