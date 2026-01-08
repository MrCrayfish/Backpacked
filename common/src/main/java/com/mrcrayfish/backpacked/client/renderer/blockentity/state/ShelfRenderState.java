package com.mrcrayfish.backpacked.client.renderer.blockentity.state;

import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ShelfRenderState extends BlockEntityRenderState
{
    public ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
    public @Nullable ClientBackpack backpack;
    public Direction direction;
    public @Nullable Component nameplate;
    public int recallQueueCount;
}
