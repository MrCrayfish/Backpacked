package com.mrcrayfish.backpacked.client.renderer.backpack;

public enum RenderMode
{
    ALL,
    MODELS_ONLY,
    EFFECTS_ONLY;

    public boolean canDrawModels()
    {
        return this == ALL || this == MODELS_ONLY;
    }

    public boolean canShowEffects()
    {
        return this == ALL || this == EFFECTS_ONLY;
    }
}
