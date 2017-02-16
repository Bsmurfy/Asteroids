package bpm7175;

/**
 * Code for an Asteroids game by Brendan Murphy.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Primary class for the game
 */
public class Asteroids extends JComponent implements Runnable, KeyListener 
{

	private static final long serialVersionUID = 1L;
	//the main thread becomes the game loop
    private Thread gameloop;
    private Graphics2D g2d = null;
    //toggle for drawing bounding boxes
    private boolean showBounds;
    private final int FRAMEWIDTH = 800;
    private final int FRAMEHEIGHT = 700;

    private int asteroidNumber = 10;
    private String shipColor = "white";
    private ArrayList<Asteroid> ast;
    private int aliveAsteroids;
    private AsteroidDebris[] astDebris = new AsteroidDebris[5];
    private ShipDebris[] shipDeb = new ShipDebris[12];
    private static int NUM_BULLETS = 10;
    private Bullet[] bullets;
    private boolean isLoaded = true;
    private boolean checkExploding = false;
    private boolean checkShipExploding = false;
    private Ship ship;
    private static Asteroids a;
    private static boolean isRunning;
    private boolean ingame = true;

    //create the identity transform (0,0)
    private AffineTransform identity;
    private Random rand;
    private JFrame frame;
    private boolean keyLeft;
    private boolean keyRight;
    private boolean keyUp;
    private boolean keyDown;

    public Asteroids()
    {
        showBounds = false;
        ast = new ArrayList<Asteroid>();
        bullets = new Bullet[NUM_BULLETS];
        for (int i = 0; i < NUM_BULLETS; i++)
        {
           bullets[i] = new Bullet();
        }
        for (int i = 0; i < 5; i++)
        {
           astDebris[i] = new AsteroidDebris();
        }
        for (int i = 0; i < 12; i++)
        {
           shipDeb[i] = new ShipDebris();
        }
        ship = new Ship();
        //g2d = null;

        frame = new JFrame("Asteroids");
        identity = new AffineTransform();
        rand = new Random();
        frame.setSize(FRAMEWIDTH, FRAMEHEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
    }

    public static void main(String [] args) 
    {
        Asteroids a = new Asteroids();
        Asteroids.isRunning = true;
        a.go();
    }
    
    public void promptInput()
    {
    	JTextField xField = new JTextField(5);
    	JTextField yField = new JTextField(5);
    	Object[] choices = {"Submit"};

    	JPanel myPanel = new JPanel();
    	myPanel.add(new JLabel("Number of Asteroids (1 through 20):"));
    	myPanel.add(xField);
    	myPanel.add(Box.createHorizontalStrut(15)); // a spacer
    	myPanel.add(new JLabel("Ship Color:"));
    	myPanel.add(yField);

    	JOptionPane.showOptionDialog(null, myPanel, 
    			"Welcome to Asteroids", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
    	try
    	{
    		asteroidNumber = Integer.parseInt(xField.getText());
    	}
    	catch (NumberFormatException e)
    	{
    		asteroidNumber = 10;
    	}
    	if (asteroidNumber < 1 || asteroidNumber > 20)
    	{
    		asteroidNumber = 10;
    	}
    	aliveAsteroids = asteroidNumber;
    	shipColor = yField.getText();
    }

    public void go() 
    {

    	promptInput();

    	Asteroids.isRunning = true;

    	//set up the ship
    	ship.setX(FRAMEWIDTH / 2);
    	ship.setY(FRAMEHEIGHT / 2);

        //set up the bullets
        for (int n = 0; n < NUM_BULLETS; n++) 
        {
            bullets[n] = new Bullet();
        }

        //set up the asteroids
        for (int n = 0; n < asteroidNumber; n++) 
        {
            Asteroid a = new Asteroid();
            a.setX((double)rand.nextInt(FRAMEWIDTH)+20);
            a.setY((double)rand.nextInt(FRAMEHEIGHT)+20);
            a.setMoveAngle(rand.nextInt(360));
            double ang = a.getMoveAngle() - 90;
            a.setVelX(calcAngleMoveX(ang));
            a.setVelY(calcAngleMoveY(ang));
            ast.add(a);
        }

        addKeyListener(this);
        requestFocusInWindow();
        gameloop = new Thread(this);
        gameloop.start();
    }

    public void paintComponent(Graphics g) 
    {
        g2d = (Graphics2D) g;
        //start off transforms at identity
        g2d.setTransform(identity);

        //erase the background
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, getSize().width, getSize().height);

        g2d.setColor(Color.WHITE);
        g2d.drawString("Asteroids: " + aliveAsteroids, 5, 10);

        if (ingame)
        {
           //draw the game graphics
           drawShip();
           drawBullets();
           drawAsteroids();
           drawAsteroidDebris();
           drawShipDebris();
           if (keyUp == true)
           {
             drawThrust();
           }
        }
        else
        {
            if (aliveAsteroids == 0)
            {
                g2d.setPaint(Color.GREEN);
                g2d.fillRect(0, 0, getSize().width, getSize().height);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 18));
                String congratsMsg = "Congratulations, you won! Press 'R' to restart the game.";
                int congratsWidth = g2d.getFontMetrics().stringWidth(congratsMsg);
                g2d.drawString(congratsMsg, (getSize().width / 2) - (congratsWidth / 2), getSize().height/2);
            }
            else
            {
                g2d.setPaint(Color.RED);
                g2d.fillRect(0, 0, getSize().width, getSize().height);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 18));
                String gameOverMsg = "Oh no, you were hit! Game over. Press 'R' to restart the game.";
                int gameOverWidth = g2d.getFontMetrics().stringWidth(gameOverMsg);
                g2d.drawString(gameOverMsg, (getSize().width / 2) - (gameOverWidth / 2), getSize().height/2);
            }
        }
    }

    public void drawShip() 
    {
       if (ship.isAlive())
          {
           //draw the ship
           g2d.setTransform(identity);
           g2d.translate(ship.getX(), ship.getY());
           g2d.rotate(Math.toRadians(ship.getFaceAngle()));
           if (shipColor.equalsIgnoreCase("red"))
           {
              g2d.setColor(Color.RED);
           }
           else if (shipColor.equalsIgnoreCase("blue"))
           {
              g2d.setColor(Color.BLUE);
           }
           else if (shipColor.equalsIgnoreCase("green"))
           {
              g2d.setColor(Color.GREEN);
           }
           else if (shipColor.equalsIgnoreCase("orange"))
           {
              g2d.setColor(Color.ORANGE);
           }
           else if (shipColor.equalsIgnoreCase("yellow"))
           {
              g2d.setColor(Color.YELLOW);
           }
           else if (shipColor.equalsIgnoreCase("pink"))
           {
              g2d.setColor(Color.PINK);
           }
           else if (shipColor.equalsIgnoreCase("magenta"))
           {
              g2d.setColor(Color.MAGENTA);
           }
           else if (shipColor.equalsIgnoreCase("cyan"))
           {
              g2d.setColor(Color.CYAN);
           }
           else
           {
           g2d.setColor(Color.WHITE);
           }
           g2d.fill(ship.getShape());
           //draw bounding rectangle around ship
           if (showBounds) 
           {
               g2d.setTransform(identity);
               g2d.setColor(Color.BLUE);
               g2d.draw(ship.getBounds());
           }
       }
    }
    
    public void drawShipDebris()
    {
       for (ShipDebris d : shipDeb) 
       {
          if (d != null)
          {
            if (d.isAlive())
            {
                g2d.setTransform(identity);
                g2d.translate(d.getX(), d.getY());
                g2d.rotate(Math.toRadians(d.getMoveAngle()));
                if (shipColor.equalsIgnoreCase("red"))
                {
                   g2d.setColor(Color.RED);
                }
                else if (shipColor.equalsIgnoreCase("blue"))
                {
                   g2d.setColor(Color.BLUE);
                }
                else if (shipColor.equalsIgnoreCase("green"))
                {
                   g2d.setColor(Color.GREEN);
                }
                else if (shipColor.equalsIgnoreCase("orange"))
                {
                   g2d.setColor(Color.ORANGE);
                }
                else if (shipColor.equalsIgnoreCase("yellow"))
                {
                   g2d.setColor(Color.YELLOW);
                }
                else if (shipColor.equalsIgnoreCase("pink"))
                {
                   g2d.setColor(Color.PINK);
                }
                else if (shipColor.equalsIgnoreCase("magenta"))
                {
                   g2d.setColor(Color.MAGENTA);
                }
                else if (shipColor.equalsIgnoreCase("cyan"))
                {
                   g2d.setColor(Color.CYAN);
                }
                else
                {
                g2d.setColor(Color.WHITE);
                }
                g2d.fill(d.getShape());
            }
            if (d.isExploding())
            {
               if (checkShipExploding == true)
               {
                  Runnable explode = new Runnable() {
                     public void run() {
                         checkShipExploding = false;
                     }
                  };
                  ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
                  executor.schedule(explode, 200, TimeUnit.MILLISECONDS);
               }
            }
            if (checkShipExploding == false)
            {
               d.setExploding(false);
               d.setAlive(false);
            }
         }
      }
    }

    public void drawThrust()
    {
       if (ship.isAlive())
       {
          Thrust t = new Thrust();
          g2d.setTransform(identity);
          g2d.translate(ship.getX() - calcAngleMoveX(ship.getMoveAngle()) * 14, ship.getY() - calcAngleMoveY(ship.getMoveAngle()) * 14);
          g2d.rotate(Math.toRadians(ship.getFaceAngle()));
          g2d.setColor(Color.ORANGE);
          g2d.fill(t.getShape());
       }
    }
    
    public void drawBullets() 
    {
       if (bullets != null)
       {
          for (Bullet bullet : bullets) 
          {
              if (bullet.isAlive()) 
              {
                  //draw the bullet
                  g2d.setTransform(identity);
                  g2d.translate(bullet.getX(), bullet.getY());
                  g2d.setColor(Color.RED);
                  g2d.draw(bullet.getShape());
              }
          }
       }
    }

    public void drawAsteroids() 
    {
       if (ast != null)
       {
          for (Asteroid a : ast) 
          {
              if (a.isAlive()) 
              {
                  //draw the asteroid
                  g2d.setTransform(identity);
                  g2d.translate(a.getX(), a.getY());
                  g2d.rotate(Math.toRadians(a.getMoveAngle()));
                  g2d.setColor(Color.DARK_GRAY);
                  g2d.fill(a.getShape());

                  //draw bounding rectangle
                  if (showBounds) 
                  {
                      g2d.setTransform(identity);
                      g2d.setColor(Color.BLUE);
                      g2d.draw(a.getBounds());
                  }
              }
          }
       }
    }
    
    public void drawAsteroidDebris()
    {
       for (AsteroidDebris d : astDebris) 
       {
          if (d != null)
          {
    		   if (d.isAlive())
    		   {
                g2d.setTransform(identity);
                g2d.translate(d.getX(), d.getY());
                g2d.rotate(Math.toRadians(d.getMoveAngle()));
                g2d.setColor(Color.DARK_GRAY);
                g2d.fill(d.getShape());
    		   }
    		   if (d.isExploding())
    		   {
    		      if (checkExploding == true)
    		      {
                  Runnable explode = new Runnable() {
                     public void run() {
                         checkExploding = false;
                     }
                  };
                  ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
                  executor.schedule(explode, 200, TimeUnit.MILLISECONDS);
    		      }
    		   }
    		   if (checkExploding == false)
    		   {
               d.setExploding(false);
               d.setAlive(false);
    		   }
    		}
    	}
    }

    /**
     * thread run event (game loop)
     */
    @Override
    public void run() 
    {
        //acquire the current thread
        Thread t = Thread.currentThread();
        //keep going as long as the thread is alive
        while (t == gameloop && Asteroids.isRunning) 
        {
            try 
            {
                //update the game loop
                gameUpdate();
                //approximately 50 fps
                Thread.sleep(20);
            }
            catch(InterruptedException e) 
            {
                e.printStackTrace();
            }
            repaint();
        }
    }

    /**
     * move and animate the objects in the game
     */
    private void gameUpdate() 
    {
        updateShip();
        updateBullets();
        updateAsteroids();
        checkCollisions();
        updateAsteroidDebris();
        updateShipDebris();
        
        if (ship.isAlive())
        {
           if (keyUp == true)
           {
              //up arrow adds thrust to ship (1/10 normal speed)
              ship.setMoveAngle(ship.getFaceAngle() - 90);
              if ((ship.getVelX() + calcAngleMoveX(ship.getMoveAngle()) * 0.1) <= 5
                    && (ship.getVelX() + calcAngleMoveX(ship.getMoveAngle()) * 0.1) >= -5)
              {
                 ship.incVelX(calcAngleMoveX(ship.getMoveAngle()) * 0.1);
              } 
              if ((ship.getVelY() + calcAngleMoveY(ship.getMoveAngle()) * 0.1) <= 5
                    && (ship.getVelY() + calcAngleMoveY(ship.getMoveAngle()) * 0.1) >= -5)
              {
                 ship.incVelY(calcAngleMoveY(ship.getMoveAngle()) * 0.1);
              } 
           }
           
           if (keyDown == true)
           {
             ship.incVelX(calcAngleMoveX(ship.getMoveAngle()) * -0.1);
             ship.incVelY(calcAngleMoveY(ship.getMoveAngle()) * -0.1);
           }
           
           if (keyLeft == true)
           {
              //left arrow rotates ship left 5 degrees 
              ship.incFaceAngle(-10);
              if (ship.getFaceAngle() < 0) 
                  ship.setFaceAngle(360-10);
           }
           
           if (keyRight == true)
           {
              //right arrow rotates ship right 5 degrees
              ship.incFaceAngle(10);
              if (ship.getFaceAngle() > 360) 
                  ship.setFaceAngle(10);
           }
        }
    }

    /**
     * Update the ship position based on velocity
     */
    public void updateShip() 
    {
        //update ship's X position, wrap around left/right
        ship.incX(ship.getVelX());
        if (ship.getX() < -10)
        {
            ship.setX(getSize().width + 10);
        }
        else if (ship.getX() > getSize().width + 10)
        {
            ship.setX(-10);
        }
        //update ship's Y position, wrap around top/bottom
        ship.incY(ship.getVelY());
        if (ship.getY() < -10)
        {
            ship.setY(getSize().height + 10);
        }
        else if (ship.getY() > getSize().height + 10)
        {
            ship.setY(-10);
        }
    }
    
    public void updateShipDebris()
    {
       for (ShipDebris d : shipDeb) 
       {
          if (d != null)
          {
               //update the ship debris' X value
               d.incX(d.getVelX() + calcAngleMoveX(d.getMoveAngle()) * 5);
               if (d.getX() < -20)
               {
                   d.setX(getSize().width + 20);
               }
               else if (d.getX() > getSize().width + 20)
               {
                   d.setX(-20);
               }

               //update the ship debris' Y value
               d.incY(d.getVelY() + calcAngleMoveY(d.getMoveAngle()) * 5);
               if (d.getY() < -20)
               {
                   d.setY(getSize().height + 20);
               }
               else if (d.getY() > getSize().height + 20)
               {
                   d.setY(-20);
               }
               d.setExploding(true);
               //checkExploding = true;
          }
       } 
    }

    /**
     * Update the bullets based on velocity
     */
    public void updateBullets() 
    {
        //move the bullets
        for (Bullet bullet : bullets)
        {
            if (bullet.isAlive()) 
            {
                //update bullet's x position
                bullet.incX(bullet.getVelX());
                //bullet disappears at left/right edge
                if (bullet.getX() < 0 || bullet.getX() > getSize().width)
                {
                    bullet.setAlive(false);
                }
                //update bullet's y position
                bullet.incY(bullet.getVelY());
                //bullet disappears at top/bottom edge
                if (bullet.getY() < 0 || bullet.getY() > getSize().height)
                {
                    bullet.setAlive(false);
                }
            }
        }
    }

    /**
     * Update the asteroids based on velocity
     */
    public void updateAsteroids() 
    {
        //move the asteroids
        for (Asteroid a : ast) 
        {
            if(a.isAlive()) 
            {
                //update the asteroid's X value
                a.incX(a.getVelX());
                if (a.getX() < -20)
                {
                    a.setX(getSize().width + 20);
                }
                else if (a.getX() > getSize().width + 20)
                {
                    a.setX(-20);
                }

                //update the asteroid's Y value
                a.incY(a.getVelY());
                if (a.getY() < -20)
                {
                    a.setY(getSize().height + 20);
                }
                else if (a.getY() > getSize().height + 20)
                {
                    a.setY(-20);
                }
            }
        }
    }
    
    public void updateAsteroidDebris()
        {
        //move the asteroids
        for (AsteroidDebris d : astDebris) 
        {
           if (d != null)
           {
                //update the asteroid's X value
                d.incX(d.getVelX() + calcAngleMoveX(d.getMoveAngle()) * 5);
                if (d.getX() < -20)
                {
                    d.setX(getSize().width + 20);
                }
                else if (d.getX() > getSize().width + 20)
                {
                    d.setX(-20);
                }

                //update the asteroid's Y value
                d.incY(d.getVelY() + calcAngleMoveY(d.getMoveAngle()) * 5);
                if (d.getY() < -20)
                {
                    d.setY(getSize().height + 20);
                }
                else if (d.getY() > getSize().height + 20)
                {
                    d.setY(-20);
                }
                d.setExploding(true);
                //checkExploding = true;
           }
        }
    }

    /**
     * Test asteroids for collisions with ship or bullets
     */
    public void checkCollisions() 
    {
        //check for ship and bullet collisions with asteroids
        for (Asteroid b : ast) 
        {
            if (b.isAlive()) 
            {
                //check for bullet collisions
                for (Bullet bullet : bullets)
                {
                    if (bullet.isAlive()) 
                    {
                        //perform the collision test
                        if (b.getBounds().contains(bullet.getX(), 
                                                   bullet.getY()))
                        {
                            bullet.setAlive(false);
                            aliveAsteroids--;
                            b.setAlive(false);
                            if (aliveAsteroids == 0)
                            {
                               Runnable explode = new Runnable() {
                                  public void run() {
                                     Asteroids.isRunning = false;
                                     ingame = false;
                                  }
                               };
                               ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
                               executor.schedule(explode, 1000, TimeUnit.MILLISECONDS);
                            }
                            continue;
                        }
                    }
                }

                //check for ship collision
                if (b.getBounds().intersects(ship.getBounds())) 
                {
                   ship.setAlive(false);
                   for (int i = 0; i < 12; i++)
                   {
                      if (shipDeb[i] != null)
                      {
                         shipDeb[i] = new ShipDebris();
                         shipDeb[i].setAlive(true);
                         shipDeb[i].setMoveAngle(ship.getMoveAngle() + (30 * i));
                         shipDeb[i].setX(ship.getX());
                         shipDeb[i].setY(ship.getY());
                         checkShipExploding = true;
                      }
                  }
                   Runnable explode = new Runnable() {
                      public void run() {
                         Asteroids.isRunning = false;
                         ingame = false;
                      }
                   };
                   ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
                   executor.schedule(explode, 1000, TimeUnit.MILLISECONDS);
                    continue;
                }
                
                if (!b.isAlive())
                {
                    for (int i = 0; i < 5; i++)
                    {
                       if (astDebris[i] != null)
                       {
                          astDebris[i] = new AsteroidDebris();
                          astDebris[i].setAlive(true);
                    		  astDebris[i].setMoveAngle(b.getMoveAngle() + (60 * i));
                    		  astDebris[i].setVelX(b.getVelX());
                    		  astDebris[i].setVelY(b.getVelY());
                    		  astDebris[i].setX(b.getX());
                    		  astDebris[i].setY(b.getY());
                    		  checkExploding = true;
                       }
              	    }
                }
            }
        }
        
        //check for ship collision with bullet
        for (Bullet bullet : bullets)
        {
            if (bullet.isAlive()) 
            {
                //perform the collision test
                if (ship.getBounds().contains(bullet.getX(), 
                                           bullet.getY()))
                {
                   ship.setAlive(false);
                   for (int i = 0; i < 12; i++)
                   {
                      if (shipDeb[i] != null)
                      {
                         shipDeb[i] = new ShipDebris();
                         shipDeb[i].setAlive(true);
                         shipDeb[i].setMoveAngle(ship.getMoveAngle() + (30 * i));
                         shipDeb[i].setX(ship.getX());
                         shipDeb[i].setY(ship.getY());
                         checkShipExploding = true;
                      }
                  }
                   Runnable explode = new Runnable() {
                      public void run() {
                         Asteroids.isRunning = false;
                         ingame = false;
                      }
                   };
                   ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
                   executor.schedule(explode, 1000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /**
     * key listener events
     */
    @Override
    public void keyReleased(KeyEvent k)
    {
       int keyCode = k.getKeyCode();

       //switch (keyCode) 
       //{
       if (keyCode == KeyEvent.VK_UP)
       {
          keyUp = false;          
           //break;
       }
       
       if (keyCode == KeyEvent.VK_DOWN)
       {
          keyDown = false;  
           //break;
       }
       
       if (keyCode == KeyEvent.VK_LEFT)
       {
          keyLeft = false;          
           //break;
       }

       if (keyCode == KeyEvent.VK_RIGHT)
       {
          keyRight = false;       
           //break;
       }
    }
    @Override
    public void keyTyped(KeyEvent k) { }
    @Override
    public void keyPressed(KeyEvent k) 
    {
        int keyCode = k.getKeyCode();

        //switch (keyCode) 
        //{
        if (keyCode == KeyEvent.VK_UP)
        {
           keyUp = true;          
            //break;
        }
        
        if (keyCode == KeyEvent.VK_DOWN)
        {
           keyDown = true;  
            //break;
        }
        
        if (keyCode == KeyEvent.VK_LEFT)
        {
           keyLeft = true;          
            //break;
        }

        if (keyCode == KeyEvent.VK_RIGHT)
        {
           keyRight = true;       
            //break;
        }
        
            //Ctrl, Enter, or Space can be used to fire weapon
        if (keyCode == KeyEvent.VK_CONTROL
        || keyCode == KeyEvent.VK_ENTER
        || keyCode == KeyEvent.VK_SPACE)
        {
            //fire a bullet
           for (Bullet bullet : bullets)
           {
              if (!bullet.isAlive() && isLoaded == true)
              {
                 isLoaded = false;
                 bullet.setAlive(true);
                 //point bullet in same direction ship is facing
                 //fire bullet at angle of the ship
                 bullet.setMoveAngle(ship.getFaceAngle() - 90);
                 double angle = bullet.getMoveAngle();
                 bullet.setX(ship.getX() + calcAngleMoveX(angle) * 5);
                 bullet.setY(ship.getY() + calcAngleMoveY(angle) * 5);
                 double svx = ship.getVelX();
                 double svy = ship.getVelY();
                 bullet.setVelX(svx + calcAngleMoveX(angle) * 5);
                 bullet.setVelY(svy + calcAngleMoveY(angle) * 5);

                if (isLoaded == false)
                {
                   Runnable reload = new Runnable() {
                      public void run() {
                          isLoaded = true;
                      }
                   };
                   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                   executor.schedule(reload, 200, TimeUnit.MILLISECONDS);
                }
              }
            }
        }

        if (keyCode == KeyEvent.VK_B)
        {
            //toggle bounding rectangles
            showBounds = !showBounds;
        }
            
        if (keyCode == KeyEvent.VK_R)
        {
        	//resets the game
        	frame.dispose();
        	a = new Asteroids();
        	Asteroids.isRunning = true;
        	a.go();
        }
    }

    /**
     * calculate X movement value based on direction angle
     */
    public double calcAngleMoveX(double angle) 
    {
        return (Math.cos(angle * Math.PI / 180));
    }

    /**
     * calculate Y movement value based on direction angle
     */
    public double calcAngleMoveY(double angle) 
    {
        return (Math.sin(angle * Math.PI / 180));
    }
}