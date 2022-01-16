package com.mrcrayfish.backpacked.common.tracker;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CraftingProgressTracker implements IProgressTracker
{
    protected int count;
    protected final int totalCount;
    protected final Predicate<ItemStack> predicate;

    public CraftingProgressTracker(int totalCount, Predicate<ItemStack> predicate)
    {
        this.totalCount = totalCount;
        this.predicate = predicate;
    }

    public void processCrafted(ItemStack stack, ServerPlayerEntity player)
    {
        if(this.predicate.test(stack))
        {
            this.count += stack.getCount();
            this.markForCompletionTest(player);
        }
    }

    @Override
    public boolean isComplete()
    {
        return this.count >= this.totalCount;
    }

    @Override
    public void read(CompoundNBT tag)
    {
        this.count = tag.getInt("Count");
    }

    @Override
    public void write(CompoundNBT tag)
    {
        tag.putInt("Count", this.count);
    }

    @Override
    public ITextComponent getDisplayComponent()
    {
        return ProgressFormatters.CRAFT_X_OF_X.apply(this.count, this.totalCount);
    }
}
