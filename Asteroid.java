package bpm7175;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Asteroid class derives from BaseVectorShape
 */
public class Asteroid extends BaseVectorShape 
{
    //define the asteroid polygon shape
    private int[] astx = {-20, -13,  0, 20, 22,  20,  12,   2, -10, -22, -16};
    private int[] asty = { 20,  23, 17, 20, 16, -20, -22, -14, -17, -20,  -5};

    public Asteroid() 
    {
        setShape(new Polygon(astx, asty, astx.length));
        setAlive(true);
    }
    
    //bounding rectangle
    public Rectangle getBounds() 
    {
        return new Rectangle((int)getX() - 20, (int) getY() - 20, 40, 40);
    }
}
