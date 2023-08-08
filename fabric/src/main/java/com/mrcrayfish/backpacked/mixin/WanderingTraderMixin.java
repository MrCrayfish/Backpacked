package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.data.pickpocket.PickpocketChallenge;
import com.mrcrayfish.backpacked.entity.IPickpocketChallengeHolder;
import com.mrcrayfish.backpacked.entity.LazyHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@Mixin(WanderingTrader.class)
public class WanderingTraderMixin implements IPickpocketChallengeHolder
{
    @Unique
    @Nullable
    private LazyHolder<PickpocketChallenge> backpackedPickpocketChallengeHolder;

    @Override
    public PickpocketChallenge backpackedGetPickpocketChallenge()
    {
        if(this.backpackedPickpocketChallengeHolder == null)
        {
            this.backpackedPickpocketChallengeHolder = new LazyHolder<>(new CompoundTag(), PickpocketChallenge::new);
        }
        return this.backpackedPickpocketChallengeHolder.get();
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void backpackedOnLoadUnlockTracker(CompoundTag tag, CallbackInfo ci)
    {
        this.backpackedPickpocketChallengeHolder = new LazyHolder<>(tag.getCompound("BackpackedUnlockTracker"), PickpocketChallenge::new);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void backpackedOnSaveUnlockTracker(CompoundTag tag, CallbackInfo ci)
    {
        if(this.backpackedPickpocketChallengeHolder != null)
        {
            tag.put("BackpackedUnlockTracker", this.backpackedPickpocketChallengeHolder.serialize());
        }
    }
}
