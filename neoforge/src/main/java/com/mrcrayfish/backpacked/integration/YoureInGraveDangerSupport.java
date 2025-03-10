package com.mrcrayfish.backpacked.integration;

import com.b1n_ry.yigd.compat.CompatComponent;
import com.b1n_ry.yigd.compat.InvModCompat;
import com.b1n_ry.yigd.data.DeathContext;
import com.b1n_ry.yigd.util.DropRule;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.function.Predicate;

public class YoureInGraveDangerSupport
{
    public static void init()
    {
        // Use the lowest priority so event is triggered after YIGD reload
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, YoureInGraveDangerSupport::onServerStarted);
    }

    private static void onServerStarted(ServerStartedEvent event)
    {
        InvModCompat.invCompatMods.add(new BackpackedCompat());
    }

    private static class BackpackedCompat implements InvModCompat<ItemStack>
    {
        @Override
        public String getModName()
        {
            return Constants.MOD_ID;
        }

        @Override
        public void clear(ServerPlayer player)
        {
            ModSyncedDataKeys.BACKPACK.setValue(player, ItemStack.EMPTY);
        }

        @Override
        public CompatComponent<ItemStack> readNbt(CompoundTag tag, HolderLookup.Provider provider)
        {
            return new BackpackedCompatComponent(ItemStack.parseOptional(provider, tag));
        }

        @Override
        public CompatComponent<ItemStack> getNewComponent(ServerPlayer player)
        {
            return new BackpackedCompatComponent(player);
        }
    }

    private static class BackpackedCompatComponent extends CompatComponent<ItemStack>
    {
        public BackpackedCompatComponent(ServerPlayer player)
        {
            super(player);
        }

        public BackpackedCompatComponent(ItemStack stack)
        {
            super(stack);
        }

        @Override
        public ItemStack getInventory(ServerPlayer player)
        {
            return ModSyncedDataKeys.BACKPACK.getValue(player);
        }

        @Override
        public NonNullList<ItemStack> merge(CompatComponent<?> otherComponent, ServerPlayer player)
        {
            NonNullList<ItemStack> extra = NonNullList.create();
            BackpackedCompatComponent component = (BackpackedCompatComponent) otherComponent;
            if(component.inventory.isEmpty())
                return extra;
            extra.add(component.inventory);
            ItemStack stack = this.inventory;
            if(stack.isEmpty())
            {
                extra.clear();
                this.inventory = component.inventory;
            }
            return extra;
        }

        @Override
        public NonNullList<ItemStack> storeToPlayer(ServerPlayer player)
        {
            NonNullList<ItemStack> extra = NonNullList.create();
            if(this.inventory.isEmpty())
                return extra;
            extra.add(this.inventory);
            ItemStack stack = ModSyncedDataKeys.BACKPACK.getValue(player);
            if(stack.isEmpty())
            {
                extra.clear();
                ModSyncedDataKeys.BACKPACK.setValue(player, this.inventory.copy());
            }
            return extra;
        }

        @Override
        public void handleDropRules(DeathContext context)
        {
            // No implementation
        }

        @Override
        public NonNullList<Tuple<ItemStack, DropRule>> getAsStackDropList()
        {
            NonNullList<Tuple<ItemStack, DropRule>> drops = NonNullList.create();
            if(!this.inventory.isEmpty())
            {
                DropRule rule = this.getDropRule();
                drops.add(new Tuple<>(this.inventory.copy(), rule));
            }
            return drops;
        }

        @Override
        public CompatComponent<ItemStack> filterInv(Predicate<DropRule> predicate)
        {
            ItemStack result = ItemStack.EMPTY;
            DropRule rule = this.getDropRule();
            if(predicate.test(rule))
            {
                result = this.inventory;
            }
            return new BackpackedCompatComponent(result);
        }

        @Override
        public boolean removeItem(Predicate<ItemStack> predicate, int count)
        {
            if(predicate.test(this.inventory))
            {
                this.inventory.shrink(count);
                return true;
            }
            return false;
        }

        @Override
        public void clear()
        {
            this.inventory = ItemStack.EMPTY;
        }

        @Override
        public CompoundTag writeNbt(HolderLookup.Provider provider)
        {
            return (CompoundTag) this.inventory.saveOptional(provider);
        }

        private DropRule getDropRule()
        {
            boolean keepOnDeath = Config.SERVER.backpack.keepOnDeath.get();
            return keepOnDeath ? DropRule.KEEP : DropRule.PUT_IN_GRAVE;
        }
    }
}
