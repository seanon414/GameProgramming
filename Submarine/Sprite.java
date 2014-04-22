import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.net.URL;

import java.awt.geom.*;

public class Sprite
{
    public String  name;
    public Point   position = new Point(0,0);
    
    // store rotation in degrees
    public double  angle = 0;
    
    public boolean solid = false;
    public Point   velocity = new Point(0,0);
    public Image   image;

    // stores translation and rotation data in a matrix.
    public AffineTransform transform = new AffineTransform();
    
    public boolean destroyed = false;
    
    // set methods
    public void setName(String n)
    {
        name = n;
    }
    
    public void setPosition(double px, double py)
    {
        position.x = px;
        position.y = py;
    }
    
    // load and store image
    public void setImage(String filename)
    {
        try
        {
            URL fileLocation = this.getClass().getClassLoader().getResource(filename);
            image = ImageIO.read( fileLocation );
        }
        catch (Exception e)
        {
            System.out.println("Could not load: " + filename);
        }
    }
    
    public void setAngle(double a)
    {
        angle = a;
    }
    
    public void addAngle(double da)
    {
        angle += da;
    }
    
    public void setVelocity(double vx, double vy)
    {
        velocity.x = vx;
        velocity.y = vy;
    }

    public void setSolid(boolean b)
    {
        solid = b;
    }
    
    // speed up or slow down
    public void addVelocity(double dx, double dy)
    {
        velocity.x += dx;
        velocity.y += dy;
    }
    
        // side effect: sets speed = 1.
    public void setVelocityAngle(double degrees)
    {
        double radians = Math.toRadians(degrees);
        velocity.x = Math.cos(radians);
        velocity.y = Math.sin(radians);
    }
    
    // side effect: if current speed = 0, sets angle = 0.
    public void setSpeed(double speed)
    {
        if (velocity.x == 0 && velocity.y == 0)
            setVelocityAngle(0);
        double currentSpeed = getSpeed();
        velocity.x *= (speed / currentSpeed);
        velocity.y *= (speed / currentSpeed);
    }
    
    public double getSpeed()
    {
        return Math.sqrt( velocity.x * velocity.x + velocity.y * velocity.y );
    }
    
    // update all ship properties
    public void update(double time)
    {
        double s = getSpeed();
        // update velocity to match angle of motion
        velocity.x = s * Math.cos( Math.toRadians(angle) );
        velocity.y = s * Math.sin( Math.toRadians(angle) );
        
        position.x += velocity.x * time;
        position.y += velocity.y * time;
        
        // update the transform that stores translation/rotation data
        transform.setToIdentity();
        transform.translate( position.x, position.y );
        transform.rotate( Math.toRadians( angle ), 
            image.getWidth(null)/2, image.getHeight(null)/2 );
    }
    
    // draw the sprite onto a layout
    public void render(Graphics2D grapher)
    {
        grapher.drawImage( image, transform, null );
    }
    
    
    // overlaps : otherSprite -> boolean
    //  determines if two sprites overlap
    //  (average circle method)
    public boolean overlaps(Sprite otherSprite)
    {
        // this: Sprite1, center (cx1,cy1), radius r1.
        // otherSprite:   center (cx2,cy2), radius r2.
        int w1  = this.image.getWidth(null);
        int h1  = this.image.getHeight(null);
        int cx1 = (int)this.position.x + w1 / 2;
        int cy1 = (int)this.position.y + h1 / 2;
        int r1  = ((w1 / 2) + (h1 / 2)) / 2;
        
        int w2  = otherSprite.image.getWidth(null);
        int h2  = otherSprite.image.getHeight(null);
        int cx2 = (int)otherSprite.position.x + w2 / 2;
        int cy2 = (int)otherSprite.position.y + h2 / 2;
        int r2  = ((w2 / 2) + (h2 / 2)) / 2;
        
        double d = Math.sqrt( (cy2 - cy1)*(cy2 - cy1) 
            + (cx2 - cx1)*(cx2 - cx1)  );
        
        return (d < (r1 + r2));  
    }
    
    public void bound(int layoutW, int layoutH)
    {
        if (position.x < 0)
        {
            position.x = 0.01;
            setSpeed(0.01);
        }
        if (position.y < 0)
        {
            position.y = 0.01;
            setSpeed(0.01);
        }
        if (position.x + image.getWidth(null) > layoutW)
        {
            position.x = layoutW - image.getWidth(null) - 0.01;
            setSpeed(0.01);
        }
        if (position.y + image.getHeight(null) > layoutH)
        {
            position.y = layoutH - image.getHeight(null) - 0.01;        
            setSpeed(0.01);
        }
    }

    public void wrap(int layoutW, int layoutH)
    {
        if (position.x + image.getWidth(null) < 0)
            position.x = layoutW;
        if (position.y + image.getHeight(null) < 0)
            position.y = layoutH;
        if (position.x > layoutW)
            position.x = -image.getWidth(null);
        if (position.y > layoutH)
            position.y = -image.getHeight(null);        
    }
    
}