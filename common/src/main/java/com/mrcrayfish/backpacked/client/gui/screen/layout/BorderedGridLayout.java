package com.mrcrayfish.backpacked.client.gui.screen.layout;

import net.minecraft.client.gui.layouts.GridLayout;

public class BorderedGridLayout extends GridLayout
{
    private int border;

    public BorderedGridLayout() {}

    public BorderedGridLayout(int x, int y)
    {
        super(x, y);
    }

    public BorderedGridLayout border(int size)
    {
        int diff = size - this.border;
        this.setX(this.getX() + diff);
        this.setY(this.getY() + diff);
        this.border = size;
        return this;
    }

    public int getBorder()
    {
        return this.border;
    }
    
    @Override
    public int getX()
    {
        return super.getX() - this.border;
    }

    @Override
    public int getY()
    {
        return super.getY() - this.border;
    }

    @Override
    public void setX(int x)
    {
        super.setX(x + this.border);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y + this.border);
    }

    @Override
    public int getWidth()
    {
        return super.getWidth() + this.border + this.border;
    }

    @Override
    public int getHeight()
    {
        return super.getHeight() + this.border + this.border;
    }
}
