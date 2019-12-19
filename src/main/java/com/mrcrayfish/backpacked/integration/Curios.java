package com.mrcrayfish.backpacked.integration;

/**
 * Author: MrCrayfish
 */
public class Curios
{
    //TODO reimplement when possible

    /*public static ItemStack getBackpackStack(PlayerEntity player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        LazyOptional<ICurioItemHandler> optional = CuriosAPI.getCuriosHandler(player);
        optional.ifPresent(handler -> backpack.set(handler.getStackInSlot("backpacked", 0)));
        return backpack.get();
    }

    public static ICapabilityProvider createBackpackProvider()
    {
        return CapCurioItem.createProvider(new ICurio()
        {
            @Override
            public void onEquipped(String identifier, LivingEntity livingEntity)
            {
                System.out.println("YO");
            }

            @Override
            public void playEquipSound(LivingEntity entity)
            {
                entity.world.playSound((PlayerEntity)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            @Override
            public boolean canRightClickEquip()
            {
                return true;
            }

            @Override
            public boolean shouldSyncToTracking(String identifier, LivingEntity livingEntity)
            {
                return true;
            }

            @Nonnull
            @Override
            public DropRule getDropRule(LivingEntity livingEntity)
            {
                return Config.COMMON.keepBackpackOnDeath.get() ? DropRule.ALWAYS_KEEP : DropRule.DEFAULT;
            }
        });
    }*/
}
