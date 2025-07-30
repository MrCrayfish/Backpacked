package com.mrcrayfish.backpacked.blockentity;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.common.backpack.BackpackState;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModSounds;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackShelfMenu;
import com.mrcrayfish.backpacked.inventory.container.LockedContainer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
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
    public static final int SIZE = 1;

    private final SimpleContainer container = new ShelfContainer(this);
    private BackpackShelfContainer inventory = null;

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
    public Container getInventory()
    {
        return this.inventory;
    }

    public ItemStack getBackpackWithContents()
    {
        ItemStack stack = this.getBackpack();
        if(!stack.isEmpty())
        {
            this.copyInventoryToStack(stack);
        }
        return stack;
    }

    public InteractionResult interact(Player player)
    {
        if(player instanceof ServerPlayer serverPlayer)
        {
            if(!this.container.getItem(0).isEmpty() && !serverPlayer.isCrouching())
            {
                this.openBackpackInventory(serverPlayer);
                return InteractionResult.SUCCESS;
            }
            this.openShelfManagement(serverPlayer);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    private void openBackpackInventory(ServerPlayer player)
    {
        this.getBackpackInventory().ifPresent(inventory ->
        {
            this.getBackpackItem().ifPresent(backpackItem ->
            {
                ItemStack backpack = this.container.getItem(0);
                Component title = backpack.has(DataComponents.CUSTOM_NAME) ? backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
                int cols = backpackItem.getColumnCount();
                int rows = backpackItem.getRowCount();
                UnlockedSlots slots = backpackItem.getUnlockedSlots(backpack);
                if(slots != null)
                {
                    Services.BACKPACK.openBackpackScreen(player, inventory, cols, rows, false, slots, 0, 1, title);
                }
            });
        });
    }

    public void openShelfManagement(ServerPlayer player)
    {
        player.openMenu(new SimpleMenuProvider((windowId, playerInventory, player1) -> {
            return new BackpackShelfMenu(windowId, playerInventory, new ManagementInventory(player), this.container);
        }, Component.translatable("container.backpack_shelf")));
    }

    public void copyInventoryToStack(ItemStack stack)
    {
        if(stack.isEmpty())
            return;

        if(this.inventory != null)
        {
            stack.set(DataComponents.CONTAINER, this.inventory.createContents());
        }
    }

    private Optional<LockedContainer> getBackpackInventory()
    {
        this.updateInventory();
        return Optional.ofNullable(this.inventory);
    }

    private void updateInventory()
    {
        if(this.level instanceof ServerLevel)
        {
            ItemStack stack = this.container.getItem(0);
            if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem)
            {
                if(this.inventory != null)
                {
                    this.resizeInventory();
                }
                else
                {
                    ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                    this.inventory = new BackpackShelfContainer(this, this.getBackpackSize());
                    this.inventory.copyFrom(contents);
                    this.setChanged();
                }
            }
            else
            {
                this.inventory = null;
                this.setChanged();
            }
        }
    }

    private void resizeInventory()
    {
        int backpackSize = this.getBackpackSize();
        if(this.inventory != null && this.inventory.getState().isChanged())
        {
            Container oldInventory = this.inventory;
            this.inventory = new BackpackShelfContainer(this, backpackSize);
            InventoryHelper.mergeInventoryOrSpawnIntoLevel(oldInventory, this.inventory, this.level, Vec3.atCenterOf(this.worldPosition));
            this.setChanged();
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        CompoundTag containerTag = tag.getCompound("Container");
        ContainerHelper.loadAllItems(containerTag, this.container.getItems(), provider);
        ItemStack backpack = ItemStack.parseOptional(provider, tag.getCompound("Backpack"));
        this.container.setItem(0, backpack);
        this.inventory = backpack.isEmpty() ? null : new BackpackShelfContainer(this, this.getBackpackSize());
        if(this.inventory != null)
        {
            this.inventory.load(tag, provider);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        CompoundTag containerTag = new CompoundTag();
        ContainerHelper.saveAllItems(containerTag, this.container.getItems(), provider);
        tag.put("Container", containerTag);
        tag.put("Backpack", this.container.getItem(0).saveOptional(provider));
        if(this.inventory != null)
        {
            this.inventory.save(tag, provider);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();
        tag.put("Backpack", this.container.getItem(0).saveOptional(provider));
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
        return this.container.getItem(0);
    }

    public Direction getDirection()
    {
        return this.getBlockState().getValue(ShelfBlock.FACING);
    }

    @Nullable
    private UnlockedSlots getUnlockedSlots()
    {
        ItemStack stack = this.container.getItem(0);
        if(stack.getItem() instanceof BackpackItem item)
        {
            return item.getUnlockedSlots(stack);
        }
        return null;
    }

    //TODO fabric version?
    /*public AABB getRenderBoundingBox()
    {
        return Shapes.block().bounds().inflate(0.5).move(this.worldPosition);
    }*/

    private Optional<BackpackItem> getBackpackItem()
    {
        ItemStack backpack = this.container.getItem(0);
        if(backpack.getItem() instanceof BackpackItem item)
        {
            return Optional.of(item);
        }
        return Optional.empty();
    }

    private int getBackpackSize()
    {
        return this.getBackpackItem().map(item -> item.getRowCount() * item.getColumnCount()).orElse(0);
    }

    @Override
    public void setChanged()
    {
        super.setChanged();

        /*if(this.level instanceof ServerLevel)
        {
            ItemStack backpack = this.container.getItem(0);
            if(!backpack.isEmpty())
            {
                this.copyInventoryToStack(backpack);
            }
        }*/
    }

    public static class BackpackShelfContainer extends LockedContainer
    {
        private final ShelfBlockEntity entity;
        private final BackpackState state;

        public BackpackShelfContainer(ShelfBlockEntity entity, int size)
        {
            super(size);
            this.entity = entity;
            this.state = BackpackState.create(this.entity.getBackpack());
        }

        @Override
        protected UnlockedSlots getUnlockedSlots()
        {
            UnlockedSlots slots = this.entity.getUnlockedSlots();
            return slots != null ? slots : UnlockedSlots.ALL;
        }

        @Override
        public void setChanged()
        {
            this.entity.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack)
        {
            return !BackpackSlot.isBannedItem(stack) && super.canPlaceItem(slot, stack);
        }

        @Override
        public boolean stillValid(Player player)
        {
            return this.entity.inventory == this && !this.entity.getBackpack().isEmpty() && !this.entity.remove;
        }

        public ItemStack getBackpack()
        {
            return this.entity.getBackpack();
        }

        public ShelfBlockEntity getBlockEntity()
        {
            return this.entity;
        }

        public BackpackState getState()
        {
            return this.state;
        }
    }

    public static class ShelfContainer extends SimpleContainer
    {
        private final ShelfBlockEntity shelf;

        public ShelfContainer(ShelfBlockEntity shelf)
        {
            super(1);
            this.shelf = shelf;
        }

        @Override
        public void setChanged()
        {
            Level level = this.shelf.level;
            if(level instanceof ServerLevel)
            {
                this.shelf.updateInventory();
                this.shelf.setChanged();
                BlockEntityUtil.sendUpdatePacket(this.shelf);
            }
        }

        @Override
        public void setItem(int slot, ItemStack stack)
        {
            super.setItem(slot, stack);
            if(this.shelf.level instanceof ServerLevel level)
            {
                float pitch = stack.isEmpty() ? 0.75F : 1.0F;
                level.playSound(null, this.shelf.worldPosition, ModSounds.ITEM_BACKPACK_PLACE.get(), SoundSource.BLOCKS, 1.0F, pitch);
            }
        }

        @Override
        public ItemStack removeItem(int slot, int count)
        {
            ItemStack stack = ContainerHelper.removeItem(this.getItems(), slot, count);
            if(!stack.isEmpty())
            {
                this.shelf.copyInventoryToStack(stack);
                this.setChanged();
            }
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot)
        {
            ItemStack stack = super.removeItemNoUpdate(slot);
            if(!stack.isEmpty())
            {
                this.shelf.copyInventoryToStack(stack);
            }
            this.shelf.updateInventory();
            return stack;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack)
        {
            return stack.getItem() instanceof BackpackItem;
        }

        @Override
        public int getMaxStackSize()
        {
            return 1;
        }

        public ShelfBlockEntity getShelf()
        {
            return this.shelf;
        }
    }
}
