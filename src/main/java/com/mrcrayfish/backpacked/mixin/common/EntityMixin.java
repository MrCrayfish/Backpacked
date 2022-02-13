package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.IMovedAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(Entity.class)
public class EntityMixin implements IMovedAccess
{
    @Unique
    public boolean backpackedMoved;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tickHead(CallbackInfo ci)
    {
        this.backpackedMoved = false;
    }

    @Inject(method = "move", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;nextStep()F"))
    public void moveStep(MoverType type, Vec3 delta, CallbackInfo ci)
    {
        this.backpackedMoved = true;
    }

    @Override
    public boolean backpackedMoved()
    {
        return this.backpackedMoved;
    }
}
