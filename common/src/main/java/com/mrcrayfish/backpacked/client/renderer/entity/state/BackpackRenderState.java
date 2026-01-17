package com.mrcrayfish.backpacked.client.renderer.entity.state;

import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.LevelDataState;
import com.mrcrayfish.backpacked.client.renderer.backpack.LivingEntityDataState;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class BackpackRenderState
{
    public CosmeticProperties cosmeticProperties;
    public @Nullable BackpackRenderer renderer;
    public boolean bobbing;
    public boolean visible;
    public Identifier baseModel;
    public Identifier strapsModel;
    public double horizontalDelta;
    public @Nullable LivingEntityDataState entityData;
    public @Nullable LevelDataState levelData;
    public int backpackScale;
    public @Nullable ItemTransforms transforms;
}
