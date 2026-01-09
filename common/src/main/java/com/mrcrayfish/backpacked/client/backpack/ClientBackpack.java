package com.mrcrayfish.backpacked.client.backpack;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import net.minecraft.resources.Identifier;

/**
 * Author: MrCrayfish
 */
public final class ClientBackpack extends Backpack
{
    private final Identifier baseModel;
    private final Identifier strapsModel;

    public ClientBackpack(Backpack backpack)
    {
        super(backpack.getUnlockChallenge());
        this.setup(backpack.getId());
        Identifier id = backpack.getId();
        String name = "backpacked/" + id.getPath();
        this.baseModel = Identifier.fromNamespaceAndPath(id.getNamespace(), name);
        this.strapsModel = Identifier.fromNamespaceAndPath(id.getNamespace(), name + "_straps");
    }

    public Identifier getBaseModel()
    {
        this.checkSetup();
        return this.baseModel;
    }

    public Identifier getStrapsModel()
    {
        this.checkSetup();
        return this.strapsModel;
    }
}
