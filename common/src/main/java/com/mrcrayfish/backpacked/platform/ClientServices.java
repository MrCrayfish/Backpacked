package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IClientHelper;

/**
 * Author: MrCrayfish
 */
public class ClientServices
{
    public static final IClientHelper CLIENT = Services.load(IClientHelper.class);
}
