package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IModelHelper;
import com.mrcrayfish.backpacked.platform.services.IScreenHelper;

/**
 * Author: MrCrayfish
 */
public class ClientServices
{
    public static final IScreenHelper SCREEN = Services.load(IScreenHelper.class);
    public static final IModelHelper MODEL = Services.load(IModelHelper.class);
}
