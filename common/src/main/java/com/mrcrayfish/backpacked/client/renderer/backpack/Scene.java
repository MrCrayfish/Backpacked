package com.mrcrayfish.backpacked.client.renderer.backpack;

public enum Scene // TODO DONE
{
    CUSTOMISATION_MENU,
    ON_ENTITY,
    ON_SHELF,
    ITEM;

    public boolean isCustomisationMenu()
    {
        return this == Scene.CUSTOMISATION_MENU;
    }
}
