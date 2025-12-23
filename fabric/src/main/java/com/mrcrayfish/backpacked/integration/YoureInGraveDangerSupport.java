package com.mrcrayfish.backpacked.integration;

import com.b1n_ry.yigd.compat.CompatComponent;
import com.b1n_ry.yigd.compat.InvModCompat;
import com.b1n_ry.yigd.data.DeathContext;
import com.b1n_ry.yigd.data.GraveItem;
import com.b1n_ry.yigd.events.LoadModCompatEvent;
import com.b1n_ry.yigd.util.DropRule;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class YoureInGraveDangerSupport
{
    public static void init()
    {
        LoadModCompatEvent.EVENT.register(list -> {
            list.add(new BackpackedCompat());
        });
    }

   private static class BackpackedCompat implements InvModCompat<List<Pair<Integer, GraveItem>>>
    {
        @Override
        public String getModName()
        {
            return Constants.MOD_ID;
        }

        @Override
        public void clear(ServerPlayer player)
        {
            BackpackHelper.removeAllBackpacks(player);
        }

        @Override
        public CompatComponent<List<Pair<Integer, GraveItem>>> readNbt(CompoundTag tag, HolderLookup.Provider provider)
        {
            List<Pair<Integer, GraveItem>> backpacks = new ArrayList<>();
            if(tag.contains("Backpacks", Tag.TAG_LIST))
            {
                ListTag list = tag.getList("Backpacks", Tag.TAG_COMPOUND);
                list.forEach(nbt -> {
                    if(nbt instanceof CompoundTag slotTag) {
                        int slot = slotTag.getInt("Slot");
                        ItemStack stack = ItemStack.parseOptional(provider, slotTag.getCompound("Item"));
                        DropRule dropRule = slotTag.getBoolean("Recalled") ? DropRule.DESTROY : DropRule.PUT_IN_GRAVE;
                        backpacks.add(Pair.of(slot, new  GraveItem(stack, dropRule)));
                    }
                });
            }
            return new BackpackedCompatComponent(backpacks);
        }

        @Override
        public CompatComponent<List<Pair<Integer, GraveItem>>> getNewComponent(ServerPlayer player)
        {
            return new BackpackedCompatComponent(player);
        }
    }

    private static class BackpackedCompatComponent extends CompatComponent<List<Pair<Integer, GraveItem>>>
    {
        public BackpackedCompatComponent(ServerPlayer player)
        {
            super(player);
        }

        public BackpackedCompatComponent(List<Pair<Integer, GraveItem>> backpacks)
        {
            super(backpacks);
        }

        @Override
        public List<Pair<Integer, GraveItem>> getInventory(ServerPlayer player)
        {
            List<Pair<Integer, GraveItem>> list = new ArrayList<>();
            NonNullList<ItemStack> backpacks = BackpackHelper.getBackpacks(player);
            for(int i = 0; i < backpacks.size(); i++)
            {
                ItemStack stack = backpacks.get(i);
                if(!stack.isEmpty())
                {
                    list.add(new Pair<>(i, new GraveItem(stack.copy(), DropRule.PUT_IN_GRAVE)));
                }
            }
            return list;
        }

        @Override
        public NonNullList<GraveItem> merge(CompatComponent<?> otherComponent, ServerPlayer player)
        {
            BackpackedCompatComponent component = (BackpackedCompatComponent) otherComponent;
            this.inventory.addAll(component.inventory);
            return NonNullList.create();
        }

        @Override
        public NonNullList<ItemStack> storeToPlayer(ServerPlayer player)
        {
            NonNullList<ItemStack> extra = NonNullList.create();
            this.inventory.forEach(pair -> {
                int index = pair.getFirst();
                GraveItem graveItem = pair.getSecond();
                if(BackpackHelper.getBackpackStack(player, index).isEmpty()) {
                    if(BackpackHelper.setBackpackStack(player, graveItem.stack.copyAndClear(), index)) {
                        return;
                    }
                }
                extra.add(graveItem.stack);
            });
            return extra;
        }

        @Override
        public void handleDropRules(DeathContext context)
        {
            this.inventory.forEach(pair -> {
                int index = pair.getFirst();
                GraveItem graveItem = pair.getSecond();
                ItemStack stack = graveItem.stack;
                if(this.hasRecallAugment(stack)) {
                    ServerPlayer player = context.player();
                    RecallAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.RECALL.get());
                    if(augment != null && AugmentHandler.recallBackpack(player, index, stack, augment)) {
                        graveItem.dropRule = DropRule.DESTROY;
                    }
                }
            });
        }

        @Override
        public NonNullList<GraveItem> getAsGraveItemList()
        {
            NonNullList<GraveItem> drops = NonNullList.create();
            if(!this.inventory.isEmpty())
            {
                this.inventory.forEach(pair -> drops.add(pair.getSecond()));
            }
            return drops;
        }

        @Override
        public CompatComponent<List<Pair<Integer, GraveItem>>> filterInv(Predicate<DropRule> predicate)
        {
            List<Pair<Integer, GraveItem>> list = new ArrayList<>();
            this.inventory.forEach(pair -> {
                GraveItem graveItem = pair.getSecond();
                if(predicate.test(graveItem.dropRule)) {
                    list.add(pair);
                }
            });
            return new BackpackedCompatComponent(list);
        }

        @Override
        public boolean removeItem(Predicate<ItemStack> predicate, int count)
        {
            for(Pair<Integer, GraveItem> pair : this.inventory)
            {
                ItemStack stack = pair.getSecond().stack;
                if(predicate.test(stack))
                {
                    stack.shrink(Math.min(stack.getCount(), count));
                    return true;
                }
            }
            return false;
        }

        @Override
        public void clear()
        {
            this.inventory = new ArrayList<>();
        }

        @Override
        public CompoundTag writeNbt(HolderLookup.Provider provider)
        {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            this.inventory.forEach(pair -> {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putInt("Slot", pair.getFirst());
                GraveItem graveItem = pair.getSecond();
                slotTag.put("Item", graveItem.stack.saveOptional(provider));
                slotTag.putBoolean("Recalled", graveItem.dropRule == DropRule.DESTROY);
                list.add(slotTag);
            });
            tag.put("Backpacks", list);
            return tag;
        }

        private boolean hasRecallAugment(ItemStack stack)
        {
            RecallAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.RECALL.get());
            return augment != null && augment.shelfKey().isPresent();
        }
    }
}
