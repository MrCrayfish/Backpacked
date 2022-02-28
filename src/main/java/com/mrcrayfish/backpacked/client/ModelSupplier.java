package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.model.BackpackModel;

import java.util.function.Supplier;

/**
 * Prevents model class from loading on servers
 */
public interface ModelSupplier extends Supplier<BackpackModel>
{

}
