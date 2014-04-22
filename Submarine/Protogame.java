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
    Sprite submarine;

    ArrayList<Integer> activeKeys = new ArrayList<Integer>();

    // create a bunch of mine Sprites
    ArrayList<Sprite> mineList = new ArrayList<Sprite>();
    
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
        setTitle("Sean and Mik's Game With Blue Stuff!");

        // display the window
        setVisible( true );

        // run System.exit(0) when the "X" button is clicled in the JFrame
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        layout = new BufferedImage(640,480, BufferedImage.TYPE_INT_RGB);
        grapher = layout.createGraphics();

        background = new Sprite();
        background.setImage("ocean.png");

        submarine = new Sprite();
        submarine.setName("Enterprise");
        submarine.setPosition(100,100);
        submarine.setImage("submarine.png");
        submarine.setVelocity(50,0); // pixels per second.
        submarine.setAngle(2);

        
        
        for (int i = 0; i < 10; i++)
        {
            spawnMine();
        }
        
        addKeyListener(this);
    }
    
    public void spawnMine(){
        Random randy = new Random();
        Sprite mine = new Sprite();
        mine.setImage("mine2.png");
        int x = randy.nextInt(620);
        int y = randy.nextInt(460);
        mine.setPosition(x,y);
        mineList.add(mine);
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
            if(submarine.getSpeed() > 250)
                submarine.setSpeed( submarine.getSpeed());
            else
                submarine.setSpeed( submarine.getSpeed() + 5 );
        if ( activeKeys.contains(KeyEvent.VK_DOWN) )
            submarine.setSpeed( 0.95 * submarine.getSpeed() );
        if ( activeKeys.contains(KeyEvent.VK_LEFT) )
            submarine.addAngle( -3.0 );
        if ( activeKeys.contains(KeyEvent.VK_RIGHT) )
            submarine.addAngle( 3.0 );

        submarine.update( 0.017 );
        submarine.wrap(640,480);
        
        for (Sprite mine : mineList)
        {
            mine.update(0.017);
            
            if (mine.overlaps(submarine))
                mine.destroyed = true;
        }
        
        // when removing items from a list,
        //  need to go through list backwards because the items
        //  automatically reposition themselves when something is removed
        //  and we must avoid skipping items & going past the end of the list
        for (int n = mineList.size() - 1; n >= 0; n -= 1)
        {
            if ( mineList.get(n).destroyed ){ 
                mineList.remove(n);
                spawnMine();
            }
        }  
    }

    // draw shapes/text/images/etc. onto layout
    public void render()
    {
        background.render(grapher);

        for (Sprite mine : mineList)
        {
            mine.render(grapher);
        }
        
        submarine.render(grapher);

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