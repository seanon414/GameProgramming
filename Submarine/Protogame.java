import javax.swing.JFrame;

import java.awt.*;
import java.awt.image.*;

import java.awt.event.*;

import java.util.*;

public class Protogame extends JFrame implements KeyListener
{
    // a canvas to draw things on
    BufferedImage layout;
    // class with drawing methods
    Graphics2D grapher;

    // create sprites for use in game.
    Sprite background;
    Sprite ship;

    ArrayList<Integer> activeKeys = new ArrayList<Integer>();

    // create a bunch of star Sprites
    ArrayList<Sprite> starList = new ArrayList<Sprite>();
    
    // initialize : () -> void
    //  sets up parameters for the appearance of the window.
    //  also initialize any other variables we might need.
    public void initialize()
    {
        // top-left corner of window
        setLocation(0,0);

        // width and height of window
        setSize(640,480);

        // text that goes in title bar
        setTitle("Stemkoski's Awesome Game of Awesomeness!");

        // display the window
        setVisible( true );

        // run System.exit(0) when the "X" button is clicled in the JFrame
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        layout = new BufferedImage(640,480, BufferedImage.TYPE_INT_RGB);
        grapher = layout.createGraphics();

        background = new Sprite();
        background.setImage("underwater-bubbles.jpg");

        ship = new Sprite();
        ship.setName("Enterprise");
        ship.setPosition(100,100);
        ship.setImage("submarine.png");
        ship.setVelocity(50,0); // pixels per second.
        ship.setAngle(2);

        Random randy = new Random();
        
        for (int i = 0; i < 10; i++)
        {
            Sprite star = new Sprite();
            star.setImage("star.png");
            int x = randy.nextInt(640);
            int y = randy.nextInt(480);
            star.setPosition(x,y);
            starList.add(star);
        }
        
        addKeyListener(this);
    }

    // loop : () -> void
    //  repeats update (event sheet) and render (layout) methods
    public void loop()
    {
        while (true)
        {
            // wait for 17 milliseconds
            try
            {
                Thread.sleep(17);
            }
            catch (Exception e)
            {
                // I don't care. Move on to the update function.
            }

            update();
            render();
        }
    }

    public void update()
    {
        if ( activeKeys.contains(KeyEvent.VK_UP) )
            if(ship.getSpeed() > 250)
                ship.setSpeed( ship.getSpeed());
            else
                ship.setSpeed( ship.getSpeed() + 5 );
        if ( activeKeys.contains(KeyEvent.VK_DOWN) )
            ship.setSpeed( 0.95 * ship.getSpeed() );
        if ( activeKeys.contains(KeyEvent.VK_LEFT) )
            ship.addAngle( -3.0 );
        if ( activeKeys.contains(KeyEvent.VK_RIGHT) )
            ship.addAngle( 3.0 );

        ship.update( 0.017 );
        ship.wrap(640,480);
        
        for (Sprite star : starList)
        {
            star.update(0.017);
            
            if (star.overlaps(ship))
                star.destroyed = true;
        }
        
        // when removing items from a list,
        //  need to go through list backwards because the items
        //  automatically reposition themselves when something is removed
        //  and we must avoid skipping items & going past the end of the list
        for (int n = starList.size() - 1; n >= 0; n -= 1)
        {
            if ( starList.get(n).destroyed ) 
                starList.remove(n);   
        }  
    }

    // draw shapes/text/images/etc. onto layout
    public void render()
    {
        background.render(grapher);

        for (Sprite star : starList)
        {
            star.render(grapher);
        }
        
        ship.render(grapher);

        // calls paint method at next available opportunity
        repaint();
    }

    // draw layout onto the JFrame
    //  built-in; does not need to be called directly
    //  can schedule a call to paint with repaint();
    public void paint(Graphics g)
    {
        // draw layout onto top-left corner of this JFrame.
        g.drawImage( layout, 0, 0, this );
    }

    public void keyPressed(KeyEvent e)
    {
        // extract key code from key event object
        int i = e.getKeyCode();
        Integer I = new Integer(i);
        // avoids duplicates in list
        if ( activeKeys.contains(I) )
            return;
        else
            activeKeys.add(I);
    }

    public void keyReleased(KeyEvent e)
    {
        // extract key code from key event object
        int i = e.getKeyCode();
        Integer I = new Integer(i);
        // avoids duplicates in list
        if ( activeKeys.contains(I) )
            activeKeys.remove(I);
        else
            return;
    }

    public void keyTyped(KeyEvent e)
    {

    }

    
    
    
}