package com.mrcrayfish.backpacked.common;

import java.util.List;

public interface CostModel
{
    /**
     * @return The cost type to use
     */
    PaymentType getPaymentType();

    /**
     * @return The id of the item to use for payments
     */
    String getPaymentItemId();

    /**
     * @return The InterpolateFunction type to use to calculate between min and max cost.
     */
    InterpolateFunction getInterpolateFunction();

    /**
     * @return An integer representing the minimum cost (only if {@link #useCustomCosts()} is false)
     */
    int getMinCost();

    /**
     * @return An integer representing the maximum cost (only if {@link #useCustomCosts()} is false)
     */
    int getMaxCost();

    /**
     * Determines if custom costs should be used, instead of interpolating between a min and max.
     * If this method returns true, costs will be determined by {@link #getCustomCosts()}.
     *
     * @return True if custom costs should be used
     */
    boolean useCustomCosts();

    /**
     * A list representing custom costs. How these values are used are decided by the implementation
     * of the cost model.
     *
     * @return A list of integers representing the cost
     */
    List<Integer> getCustomCosts();

    /**
     * @return A {@link SelectionFunction} defining how custom costs are selected.
     */
    SelectionFunction getCustomCostsSelectionFunction();
}
