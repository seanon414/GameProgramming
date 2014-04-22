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
    
    // create a bunch of bullet Sprites
    ArrayList<Sprite> bulletList = new ArrayList<Sprite>();
    
    // Constants for screen size
    int WIDTH = 640;
    int HEIGHT = 480;
    
    // Global variables
    int score = 0;
    
    // initialize : () -> void
    //  sets up parameters for the appearance of the window.
    //  also initialize any other variables we might need.
    public void initialize()
    {
        // top-left corner of window
        setLocation(0,0);

        // width and height of window
        setSize(WIDTH,HEIGHT);

        // text that goes in title bar
        setTitle("Sean and Mik's Game With Blue Stuff!");

        // display the window
        setVisible( true );

        // run System.exit(0) when the "X" button is clicled in the JFrame
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        layout = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
        grapher = layout.createGraphics();

        background = new Sprite();
        background.setImage("ocean.png");

        submarine = new Sprite();
        submarine.setName("Enterprise");
        submarine.setPosition(100,100);
        submarine.setImage("submarine.png");
        submarine.setVelocity(50,0); // pixels per second.
        submarine.setAngle(2);
        
        for (int i = 0; i < 15; i++)
        {
            spawnMine();
        }
        
        addKeyListener(this);
    }
    
    public void spawnMine(){
        Random randy = new Random();
        Sprite mine = new Sprite();
        mine.setImage("mine2.png");
        int x = randy.nextInt(620) + 20;
        int y = randy.nextInt(460) + 20;
        int angle = randy.nextInt(360);
        mine.setPosition(x,y);
        mine.setAngle(angle);
        mine.setVelocity(50, 50);
        if (!mine.overlaps(submarine))
            mineList.add(mine);
        else
            spawnMine();
    }
    // loop : () -> void
    //  repeats update (event sheet) and render (layout) methods
    public void loop()
    {
        while (!submarine.destroyed)
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
        submarine.wrap(WIDTH,HEIGHT);
       
        try{
            for (Sprite mine : mineList)
            {
                mine.wrap(WIDTH,HEIGHT);
                //for (Sprite bullet : bulletList){
                for (Sprite bullet : bulletList){
                    bullet.update(0.017);
                    if (bullet.overlaps(mine)){
                        mine.destroyed = true;
                        bullet.destroyed = true;
                    }
                }
                mine.update(0.017);
                
                if (mine.overlaps(submarine)){
                    mine.destroyed = true;
                    submarine.destroyed = true;
                }
            }
        }
        catch (Exception ConcurrentModificationException){
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
        
        for (int n = bulletList.size() - 1; n >= 0; n -= 1)
        {
            if ( bulletList.get(n).destroyed ){ 
                bulletList.remove(n);
                score += 100;
            }
        }
    }

    // draw shapes/text/images/etc. onto layout
    public void render()
    {
        background.render(grapher);
        Font stylish = new Font("Times New Roman", Font.BOLD, 30);
        grapher.setFont( stylish );
        
        for (Sprite mine : mineList)
            mine.render(grapher);
        
        for (int n = 0; n < bulletList.size(); n++)
            bulletList.get(n).render(grapher);
        
        if (submarine.destroyed){
            String message = "You hit a mine!";       
            grapher.drawString(message, 200, 240);
        }
        else
            submarine.render(grapher);
            
        String score_message = "Score: " + score;
        grapher.drawString(score_message, 20, 60);
            
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
        if (!submarine.destroyed){    
            // extract key code from key event object
            int i = e.getKeyCode();
            Integer I = new Integer(i);
            // avoids duplicates in list
            if ( !activeKeys.contains(I) )
                activeKeys.add(I);
        
            if (i == KeyEvent.VK_SPACE )
            {
                Sprite bullet = new Sprite();
                bullet.setImage("torpedo.png");
                bullet.setPosition( 
                    submarine.position.x + submarine.image.getWidth(null)/2 - bullet.image.getWidth(null)/2, 
                    submarine.position.y + submarine.image.getHeight(null)/2 - bullet.image.getHeight(null)/2
                );
                bullet.setAngle( submarine.angle );
                bullet.setSpeed( 100 );
                bulletList.add(bullet);
            }  
        }
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