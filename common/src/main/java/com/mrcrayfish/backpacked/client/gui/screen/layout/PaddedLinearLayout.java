package com.mrcrayfish.backpacked.client.gui.screen.layout;

import net.minecraft.client.gui.layouts.LinearLayout;

/**
 * Author: MrCrayfish
 */
public class PaddedLinearLayout extends LinearLayout implements PaddedLayout
{
    private int padding;

    public PaddedLinearLayout(Orientation orientation)
    {
        super(0, 0, orientation);
    }

    public PaddedLinearLayout padding(int size)
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

    public static PaddedLinearLayout vertical()
    {
        return new PaddedLinearLayout(Orientation.VERTICAL);
    }

    public static PaddedLinearLayout horizontal()
    {
        return new PaddedLinearLayout(Orientation.HORIZONTAL);
    }
}
