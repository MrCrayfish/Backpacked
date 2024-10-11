package com.mrcrayfish.backpacked.blockentity;

import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModSounds;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.BlockEntityUtil;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ShelfBlockEntity extends BlockEntity implements IOptionalStorage
{
    private ItemStack backpack = ItemStack.EMPTY;
    private SimpleContainer inventory = null;

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
        if(player.isCrouching())
        {
            if(!this.shelveBackpack(player))
            {
                return InteractionResult.FAIL;
            }
        }
        else if(player instanceof ServerPlayer serverPlayer)
        {
            this.openBackpackInventory(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean shelveBackpack(Player player)
    {
        Optional<ItemStack> optional = Services.BACKPACK.getStackInBackpackSlot(player);
        if(optional.isEmpty())
            return false;

        ItemStack stack = optional.get();
        if(stack.getItem() instanceof BackpackItem || stack.isEmpty() && !this.backpack.isEmpty())
        {
            ItemStack shelvedBackpack = this.backpack.copy();
            this.copyInventoryToStack(shelvedBackpack);
            this.backpack = stack.copy();

            // Update the stack in the backpack slot
            Services.BACKPACK.setBackpackStack(player, shelvedBackpack);

            // Play a sound
            boolean removed = this.backpack.isEmpty();
            float soundPitch = removed ? 0.75F : 1.0F;
            this.level.playSound(null, this.worldPosition, ModSounds.ITEM_BACKPACK_PLACE.get(), SoundSource.BLOCKS, 1.0F, soundPitch);

            // Update the shelf inventory
            this.updateInventory(false);

            // Send changes to client and mark block entity as dirty
            BlockEntityUtil.sendUpdatePacket(this);
            this.setChanged();

            return true;
        }
        else if(!stack.isEmpty())
        {
            player.displayClientMessage(Component.translatable("message.backpacked.occupied_back_slot"), true);
            return false;
        }
        return true;
    }

    private void openBackpackInventory(ServerPlayer player)
    {
        this.getBackpackInventory().ifPresent(inventory ->
        {
            this.getBackpackItem().ifPresent(backpackItem ->
            {
                Component title = this.backpack.has(DataComponents.CUSTOM_NAME) ? this.backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
                int cols = backpackItem.getColumnCount();
                int rows = backpackItem.getRowCount();
                Services.BACKPACK.openBackpackScreen(player, inventory, cols, rows, false, title);
            });
        });
    }

    private void copyInventoryToStack(ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            this.getBackpackInventory().ifPresent(inventory ->
            {
                stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory.getItems()));
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
            ItemContainerContents contents = this.backpack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            contents.copyInto(this.inventory.getItems());
            this.backpack.remove(DataComponents.CONTAINER);
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

    private void loadBackpackItems(HolderLookup.Provider provider, CompoundTag compound)
    {
        if(compound.contains("Items", Tag.TAG_LIST))
        {
            InventoryHelper.loadAllItems(provider, compound.getList("Items", Tag.TAG_COMPOUND), this.inventory, this.level, Vec3.atCenterOf(this.worldPosition));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        this.backpack = ItemStack.parseOptional(provider, tag.getCompound("Backpack"));
        this.inventory = this.backpack.isEmpty() ? null : new ShelfContainer(this.getBackpackSize());
        if(this.inventory != null)
        {
            ContainerHelper.loadAllItems(tag, this.inventory.getItems(), provider);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.put("Backpack", this.backpack.saveOptional(provider));
        if(this.inventory != null)
        {
            ContainerHelper.saveAllItems(tag, this.inventory.getItems(), provider);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();
        tag.put("Backpack", this.backpack.saveOptional(provider));
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }

    public Direction getDirection()
    {
        return this.getBlockState().getValue(ShelfBlock.FACING);
    }

    //TODO fabric version?
    /*public AABB getRenderBoundingBox()
    {
        return Shapes.block().bounds().inflate(0.5).move(this.worldPosition);
    }*/

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
