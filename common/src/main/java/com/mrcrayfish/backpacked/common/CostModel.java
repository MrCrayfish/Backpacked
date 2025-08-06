package com.mrcrayfish.backpacked.common;

import java.util.List;

public interface CostModel
{
    InterpolateFunction getInterpolateFunction();

    int getMinCost();

    int getMaxCost();

    boolean useCustomCosts();

    List<Integer> getCustomCosts();
}
