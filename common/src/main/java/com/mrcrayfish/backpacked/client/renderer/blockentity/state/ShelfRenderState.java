package com.mrcrayfish.backpacked.client.renderer.blockentity.state;

import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class ShelfRenderState extends BlockEntityRenderState
{
    public boolean hasBackpack;
    public @Nullable BackpackRenderer renderer;
    public Direction direction;
    public @Nullable Component nameplate;
    public @Nullable Vector3fc shelfOffset;
    public Identifier baseModel;
    public Identifier strapsModel;
    public int recallQueueCount;
    public boolean playingAnimation;
    public int animationTicks;
    public float partialTick;
    public int renderTicks;

    public void applyAnimation(int start, int end, Consumer<Float> time)
    {
        if(this.animationTicks < start || this.animationTicks >= end)
            return;
        float length = end - start;
        time.accept(((this.animationTicks - start) + this.partialTick) / length);
    }
}
