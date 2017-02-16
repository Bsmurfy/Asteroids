package bpm7175;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Ship class derives from BaseVectorShape
 */
public class Ship extends BaseVectorShape 
{
    //define the ship polygon
    private int[] shipx = {-6, -3, 0, 3, 6, 0 };
    private int[] shipy = { 6, 7, 7, 7, 6, -7 };

    public Ship() 
    {
        setShape(new Polygon(shipx, shipy, shipx.length));
        setAlive(true);
    }
    
    //bounding rectangle
    public Rectangle getBounds() 
    {
        return new Rectangle((int)getX() - 6, (int) getY() - 6, 12, 12);
    }
}