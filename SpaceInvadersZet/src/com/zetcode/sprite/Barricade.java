package com.zetcode.sprite;

import javax.swing.ImageIcon;

public class Barricade extends Sprite
{
    public Barricade(int x, int y)
    {
        initBarricade(x, y);
    }

    private void initBarricade(int x, int y)
    {
        this.x = x;
        this.y = y;

        var barricadeImg = "src/images/Barricade.png";
        var ii = new ImageIcon(barricadeImg);
        setImage(ii.getImage());
    }
}
