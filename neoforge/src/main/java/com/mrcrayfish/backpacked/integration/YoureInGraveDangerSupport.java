package com.mrcrayfish.backpacked.integration;

import com.b1n_ry.yigd.compat.CompatComponent;
import com.b1n_ry.yigd.compat.InvModCompat;
import com.b1n_ry.yigd.data.DeathContext;
import com.b1n_ry.yigd.data.GraveItem;
import com.b1n_ry.yigd.events.YigdEvents;
import com.b1n_ry.yigd.util.DropRule;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class YoureInGraveDangerSupport
{
    public static void init()
    {
        // Use the lowest priority so event is triggered after YIGD reload
        NeoForge.EVENT_BUS.addListener(YoureInGraveDangerSupport::onRegisterCompat);
    }

    private static void onRegisterCompat(YigdEvents.LoadModCompatEvent event)
    {
        event.addModCompat(new BackpackedCompat());
    }

    private static class BackpackedCompat implements InvModCompat<List<Pair<Integer, ItemStack>>>
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
        public CompatComponent<List<Pair<Integer, ItemStack>>> readNbt(CompoundTag tag, HolderLookup.Provider provider)
        {
            List<Pair<Integer, ItemStack>> backpacks = new ArrayList<>();
            if(tag.contains("Backpacks", Tag.TAG_LIST))
            {
                ListTag list = tag.getList("Backpacks", Tag.TAG_COMPOUND);
                list.forEach(nbt -> {
                    if(nbt instanceof CompoundTag slotTag) {
                        int slot = slotTag.getInt("Slot");
                        ItemStack stack = ItemStack.parseOptional(provider, slotTag.getCompound("Item"));
                        backpacks.add(Pair.of(slot, stack));
                    }
                });
            }
            return new BackpackedCompatComponent(backpacks);
        }

        @Override
        public CompatComponent<List<Pair<Integer, ItemStack>>> getNewComponent(ServerPlayer player)
        {
            return new BackpackedCompatComponent(player);
        }
    }

    private static class BackpackedCompatComponent extends CompatComponent<List<Pair<Integer, ItemStack>>>
    {
        public BackpackedCompatComponent(ServerPlayer player)
        {
            super(player);
        }

        public BackpackedCompatComponent(List<Pair<Integer, ItemStack>> backpacks)
        {
            super(backpacks);
        }

        @Override
        public List<Pair<Integer, ItemStack>> getInventory(ServerPlayer player)
        {
            List<Pair<Integer, ItemStack>> list = new ArrayList<>();
            NonNullList<ItemStack> backpacks = BackpackHelper.getBackpacks(player);
            for(int i = 0; i < backpacks.size(); i++)
            {
                ItemStack stack = backpacks.get(i);
                if(!stack.isEmpty())
                {
                    list.add(new Pair<>(i, stack.copy()));
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
                ItemStack stack = pair.getSecond();
                if(BackpackHelper.getBackpackStack(player, index).isEmpty()) {
                    if(!BackpackHelper.setBackpackStack(player, stack, index)) {
                        extra.add(stack);
                    }
                }
            });
            return extra;
        }

        @Override
        public void handleDropRules(DeathContext context)
        {
            // No implementation
        }

        @Override
        public NonNullList<GraveItem> getAsGraveItemList()
        {
            NonNullList<GraveItem> drops = NonNullList.create();
            if(!this.inventory.isEmpty())
            {
                DropRule rule = this.getDropRule();
                this.inventory.forEach(pair -> {
                    ItemStack stack = pair.getSecond();
                    if(!stack.isEmpty()) {
                        drops.add(new GraveItem(stack, rule));
                    }
                });
            }
            return drops;
        }

        @Override
        public CompatComponent<List<Pair<Integer, ItemStack>>> filterInv(Predicate<DropRule> predicate)
        {
            List<Pair<Integer, ItemStack>> list = new ArrayList<>();
            DropRule rule = this.getDropRule();
            if(predicate.test(rule))
            {
                list.addAll(this.inventory);
            }
            return new BackpackedCompatComponent(list);
        }

        @Override
        public boolean removeItem(Predicate<ItemStack> predicate, int count)
        {
            for(Pair<Integer, ItemStack> pair : this.inventory)
            {
                ItemStack stack = pair.getSecond();
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
                slotTag.put("Item", pair.getSecond().saveOptional(provider));
                list.add(slotTag);
            });
            tag.put("Backpacks", list);
            return tag;
        }

        private DropRule getDropRule()
        {
            boolean keepOnDeath = Config.BACKPACK.equipable.keepOnDeath.get();
            return keepOnDeath ? DropRule.KEEP : DropRule.PUT_IN_GRAVE;
        }
    }
}
