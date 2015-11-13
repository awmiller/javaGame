/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import map.Tiles;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import map.CharacterPiece;
import map.GamePiece;
import map.Map;
import map.ObstaclePiece;
import map.Tile;

/**
 *
 * @author awmil_000
 */
public class Game extends JFrame implements Runnable{
    public static Dimension ZERO_VECTOR = new Dimension(0,0);
    public static AffineTransform ZERO_ROTATION = new AffineTransform();
    public static int FRAMES_PER_SECOND = 120;
    private static double FRAME_PERIOD = (1000/Game.FRAMES_PER_SECOND);
    
    public JFrame otherframe;
    public JPanel otherpanel;

    public static Rectangle getRectCollider(Dimension origin, Dimension size) {
        return new Rectangle(origin.width,origin.height,size.width,size.height);
    }

    public static int distance(Dimension d1, Dimension d2) {
        int dx = Math.abs(d1.width - d2.width);
        int dy = Math.abs(d1.height - d2.height);
        return dx+dy;
    }

    public static Dimension rotate(int magnitude, double heading) {
        float x = magnitude;
        float y = magnitude;
        x = (float) -(x*Math.cos(heading));
        y = (float) -(y*Math.sin(heading));
        return new Dimension(Math.round(x),Math.round(y));
    }
    
    private Thread thread;
    
    BufferedImage bimg;
    Map gameMap;
    MapView camera;
    static final int TILES_PER_DIMENSION = 10;
    static final Dimension SCREENSIZE = new Dimension(20,20);
    static final char[] controls1 = {'w','a','s','d',' '};
    static final char[] controls2 = {'i','j','k','l','b'};
            KeyController player1Controller = new KeyController(controls1);
        GamePiece player1;
        
           KeyController player2Controller = new KeyController(controls2);
        GamePiece player2;
        
    
    public Game(int sqrtMapTiles){   
        //gameMap stores persistant world data
        //constructor creates a square map with the given side length
        gameMap = new Map(SCREENSIZE);
        
//        GamePiece gob1 = new ObstaclePiece(
//                (BufferedImage) getSprite("res/island1.png"), 
//                new Dimension(200,200)
//        );
//        gameMap.add(gob1);
//        
//        bimg = (BufferedImage) getSprite("res/island2.png");
//        
//        GamePiece gob2 = new ObstaclePiece( bimg,
//                new Dimension(300,200)
//        );
//        gameMap.add(gob2);
        
        bimg = (BufferedImage) getSprite("res/tank1_strip60.png");
        player1 = new CharacterPiece(bimg,player1Controller,new Dimension(300,300));
        player2 = new CharacterPiece(bimg,player2Controller,new Dimension(600,600));
        player1 = gameMap.add(player1);
        
 
        player2 = gameMap.add(player2);
        
        //camera is a view into the gameMap
        //currently this should show the whole map
        camera = new MapView(gameMap,new Dimension(310,310),player1);
        
        gameMap.addObserver(camera);
                
        add(camera);
        setSize(camera.getDimens());

        setTitle("Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(player1Controller);
        addKeyListener(player2Controller);
        
//        otherpanel = new JPanel();
//        otherpanel.setSize(300, 300);
//        
//        otherframe.add(otherpanel);
//        otherframe.setSize(310,310);
//
//        otherframe.setTitle("Frame");
//        otherframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        otherframe.setLocationRelativeTo(null);
//        otherframe.addKeyListener(player1Controller);
//        otherframe.addKeyListener(player2Controller);
    }
    
    
    public static void main(String args[]) {
        JFrame runner = new Game(TILES_PER_DIMENSION);
        runner.setVisible(true); 
        runner.setFocusable(true);
//        ((Game)runner).otherframe.setVisible(true);
//        ((Game)runner).otherframe.setFocusable(true);
        Thread game = new Thread((Runnable) runner);
        game.start();
    }

    long ping =System.nanoTime();
    @Override
    public void run() {
        while(true){            
            camera.repaint();
        try {
            Thread.sleep((long) Game.FRAME_PERIOD);            
            long t =System.nanoTime();
            System.out.printf("\nFrame Rate %f", 1000000000/((float)(t - ping)));
            ping =t;
        } catch (InterruptedException ex) {
            System.out.print(ex);
        }
      }
    }
    
    public static Image getSprite(String url)
    {
        Image img = new BufferedImage(40,40,BufferedImage.TYPE_INT_RGB);
        try {
            img = ImageIO.read(new File(url));
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }
    
    public static Dimension add(Dimension d1,Dimension d2){
        Dimension result = new Dimension(
                d1.width+d2.width,
                d1.height+d2.height
        );
        return result;
    }

}

