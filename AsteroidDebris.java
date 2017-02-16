package bpm7175;

import java.awt.Polygon;

/**
 * AsteroidDebris class derives from BaseVectorShape
 */
public class AsteroidDebris extends BaseVectorShape
{
   //define the asteroid debris shape
    private int[] astdebx = {-4, -3,  0, 4, 5, 4, 2, 1, -2, -5, -4};
    private int[] astdeby = { 4, 5, 4, 5, 4, -5, -5, -3, -4, -5, -1};
    private boolean isExploding = false;
   
   public AsteroidDebris() 
   {
       setShape(new Polygon(astdebx, astdeby, astdebx.length));
   }

   public boolean isExploding()
   {
      return isExploding;
   }

   public void setExploding(boolean b)
   {
      this.isExploding = b;
   }
}