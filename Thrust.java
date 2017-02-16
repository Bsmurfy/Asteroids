package bpm7175;

import java.awt.Polygon;

/**
 * Thrust class derives from BaseVectorShape
 */
public class Thrust extends BaseVectorShape
{
   private int[] thrustx = {-3, 0, 3};
   private int[] thrusty = {-7, 7, -7};
   
   public Thrust() 
   {
       setShape(new Polygon(thrustx, thrusty, thrustx.length));
   }

}
