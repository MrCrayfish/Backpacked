package com.mrcrayfish.backpacked.common;

public enum Navigate // TODO DONE
{
    CURRENT(0), PREVIOUS(-1), NEXT(1);

    private final int step;

    Navigate(int step)
    {
        this.step = step;
    }

    public int step()
    {
        return this.step;
    }
}
