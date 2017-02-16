package bpm7175;

/**
 * Base vector shape class for game entities
 */

import java.awt.Shape;

public class BaseVectorShape 
{
    //variables
    private Shape shape;
    private boolean alive;
    private double x;
    private double y;
    private double velX;
    private double velY;
    private double moveAngle;
    private double faceAngle;

    public BaseVectorShape() 
    {
        shape = null;
        alive = false;
        x = 0.0;
        y = 0.0;
        velX = 0.0;
        velY = 0.0;
        moveAngle = 0.0;
        faceAngle = 0.0;
    }

    //accessor methods
    public Shape getShape() 
    { 
        return shape; 
    }

    public boolean isAlive() 
    { 
        return alive; 
    }

    public double getX() 
    { 
        return x; 
    }

    public double getY() 
    { 
        return y; 
    }

    public double getVelX() 
    { 
        return velX; 
    }

    public double getVelY() 
    { 
        return velY; 
    }

    public double getMoveAngle() 
    { 
        return moveAngle; 
    }

    public double getFaceAngle() 
    { 
        return faceAngle; 
    }


    //mutator methods
    public void setShape(Shape shape) 
    { 
        this.shape = shape; 
    }

    public void setAlive(boolean alive) 
    { 
        this.alive = alive; 
    }

    public void setX(double x) 
    { 
        this.x = x; 
    }

    public void incX(double i) 
    { 
        x += i; 
    }

    public void setY(double y) 
    { 
        this.y = y; 
    }

    public void incY(double i) 
    { 
        y += i; 
    }

    public void setVelX(double velX) 
    { 
        this.velX = velX; 
    }

    public void incVelX(double i) 
    { 
        velX += i; 
    }

    public void setVelY(double velY) 
    { 
        this.velY = velY; 
    }

    public void incVelY(double i) 
    { 
        velY += i; 
    }

    public void setFaceAngle(double angle) 
    { 
        faceAngle = angle; 
    }

    public void incFaceAngle(double i) 
    { 
        faceAngle += i; 
    }

    public void setMoveAngle(double angle) 
    { 
        moveAngle = angle; 
    }

    public void incMoveAngle(double i) 
    { 
        moveAngle += i; 
    }
}