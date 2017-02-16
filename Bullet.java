package bpm7175;

import java.awt.Rectangle;

/**
 * Bullet class derives from BaseVectorShape
 */
public class Bullet extends BaseVectorShape 
{
    public Bullet() 
    {
        //create the bullet shape
        setShape(new Rectangle(0, 0, 2, 2));
        setAlive(false);
    }

    //bounding rectangle
    public Rectangle getBounds() 
    {
        return new Rectangle((int)getX(), (int) getY(), 2, 2);
    }
}
