package com.mrcrayfish.backpacked.client.gui.screen.layout;

import net.minecraft.client.gui.layouts.GridLayout;

public class PaddedGridLayout extends GridLayout implements PaddedLayout
{
    private int padding;

    public PaddedGridLayout() {}

    public PaddedGridLayout(int x, int y)
    {
        super(x, y);
    }

    public PaddedGridLayout padding(int size)
    {
        int diff = size - this.padding;
        this.setX(this.getX() + diff);
        this.setY(this.getY() + diff);
        this.padding = size;
        return this;
    }

    @Override
    public int padding()
    {
        return this.padding;
    }

    @Override
    public int getX()
    {
        return super.getX() - this.padding;
    }

    @Override
    public int getY()
    {
        return super.getY() - this.padding;
    }

    @Override
    public void setX(int x)
    {
        super.setX(x + this.padding);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y + this.padding);
    }

    @Override
    public int getWidth()
    {
        return super.getWidth() + this.padding + this.padding;
    }

    @Override
    public int getHeight()
    {
        return super.getHeight() + this.padding + this.padding;
    }
}
