package com.mrcrayfish.backpacked.client.renderer.blockentity.state;

import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public class BackpackDockRenderState extends BlockEntityRenderState
{
    public boolean hasBackpack;
    public Direction direction;
    public Vector3fc shelfOffset;
    public Identifier baseModel;
    public Identifier strapsModel;
    public @Nullable BackpackRenderer renderer;
    public int itemLight;
    public float partialTick;
    public int tickCount;
}
