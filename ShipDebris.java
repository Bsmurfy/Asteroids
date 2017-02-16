package bpm7175;

import java.awt.Polygon;

/**
 * ShipDebris class derives from BaseVectorShape
 */
public class ShipDebris extends BaseVectorShape
{
   //define the ship debris shape
    private int[] shipdebx = {-2, -1, 0, 1, 2, 0 };
    private int[] shipdeby = { 2, 3, 3, 3, 2, -3 };
    private boolean isExploding = false;
   
   public ShipDebris() 
   {
       setShape(new Polygon(shipdebx, shipdeby, shipdebx.length));
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