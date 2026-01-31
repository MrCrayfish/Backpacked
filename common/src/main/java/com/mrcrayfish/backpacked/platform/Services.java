package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IBackpackHelper;
import com.mrcrayfish.backpacked.platform.services.IEntityHelper;
import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services
{
    public static final IBackpackHelper BACKPACK = load(IBackpackHelper.class);
    public static final IEntityHelper ENTITY = load(IEntityHelper.class);
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz)
    {
        final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}