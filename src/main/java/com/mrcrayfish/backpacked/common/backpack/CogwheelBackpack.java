package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class CogwheelBackpack extends Backpack
{
    private static Item cogwheelItem = null;
    private static Item largeCogwheelItem = null;

    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "cogwheel");

    public CogwheelBackpack()
    {
        super(ID);
    }

    @Override
    public BackpackModel getModel()
    {
        return ModelInstances.COGWHEEL;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(64, ProgressFormatters.CRAFT_X_OF_X);
    }

    @SubscribeEvent
    public void onCraftItem(PlayerEvent.ItemCraftedEvent event)
    {
        locateItemInstances();
        ItemStack craftedItem = event.getCrafting();
        if(craftedItem.getItem() == cogwheelItem || craftedItem.getItem() == largeCogwheelItem)
        {
            PlayerEntity player = event.getPlayer();
            if(!(player instanceof ServerPlayerEntity))
                return;

            UnlockTracker.get(player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTracker(ID).ifPresent(progressTracker ->
                {
                    CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                    tracker.increment(craftedItem.getCount(), (ServerPlayerEntity) player);
                });
            });
        }
    }

    private static void locateItemInstances()
    {
        if(cogwheelItem == null)
        {
            cogwheelItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("create", "cogwheel"));
        }
        if(largeCogwheelItem == null)
        {
            largeCogwheelItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("create", "large_cogwheel"));
        }
    }
}
