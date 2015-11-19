/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.BorderLayout;
import map.Tiles;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
    public static int FRAMES_PER_SECOND = 30;
    private static double FRAME_PERIOD_MILLIS = (1000/Game.FRAMES_PER_SECOND);
    private long framePeriod;
    public static boolean DRAW_DEBUG_LINES = false;
    static GraphicsConfiguration config;
    MiniMap miniMap;
    Dimension screenSplitLocation;
    public static final boolean ENABLE_MUSIC = false;
    
    static{
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = env.getDefaultScreenDevice();
    config = device.getDefaultConfiguration();
    }
    
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
    
    Map gameMap;
    MapView camera1;
    static final int TILES_PER_DIMENSION = 10;
    static final Dimension SCREENSIZE = new Dimension(6,6);
    static final char[] controls1 = {'w','a','s','d',' '};
    static final char[] controls2 = {'i','j','k','l','b'};
            KeyController player1Controller = new KeyController(controls1);
        GamePiece player1;
        
           KeyController player2Controller = new KeyController(controls2);
        GamePiece player2;
    MapView camera2;
    private final GamePanel gameView;
    
        
    
    public Game(int sqrtMapTiles){   
        
        BufferedImage bimg;
        try {
            //gameMap stores persistant world data
            //constructor creates a square map with the given side length
            
            URL url = Game.class.getResource("/res/tank1.map");
            gameMap = new Map(url.getFile());
        } catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bimg = (BufferedImage) getSprite("/res/tank1_strip60.png");
        player1 = new CharacterPiece(bimg,player1Controller,new Dimension(300,300));
        player2 = new CharacterPiece(bimg,player2Controller,new Dimension(600,600));
        player1 = gameMap.add(player1);
        
 
        player2 = gameMap.add(player2);
        
        //camera is a view into the gameMap
        //currently this should show the whole map
        camera1 = new MapView(gameMap,new Dimension(500,500),player1);
        
        camera2 = new MapView(gameMap,new Dimension(500,500),player2);
        
        gameView = new GamePanel(new Dimension(1000,500));
        
        screenSplitLocation = new Dimension(500,500);
        
        miniMap = new MiniMap(gameMap,10);
                
        add(gameView);
        
        setSize(new Dimension(1035,540));

        setTitle("Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(player1Controller);
        addKeyListener(player2Controller);
    }
    
    
    public static void main(String args[]) {
        JFrame runner = new Game(TILES_PER_DIMENSION);
//        runner.pack();
        runner.setFocusable(true);
        runner.setVisible(true); 
//        ((Game)runner).otherframe.setVisible(true);
//        ((Game)runner).otherframe.setFocusable(true);
        Thread game = new Thread((Runnable) runner);
        game.start();

        if(ENABLE_MUSIC)
        try {
            URL defaultSound = Game.class.getResource("/res/Music.mid");
            System.out.print("\n"+defaultSound.toString());
            // getClass().getSy.getResource("/images/ads/WindowsNavigationStart.wav");
//            File soundFile = new File(defaultSound.toURI());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(defaultSound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.out.print("\n"+e.toString());
        }
    }

    
    @Override
    public void run() {
        while(true){     
            long ping = System.currentTimeMillis();
            gameMap.moveAll();
//            camera1.repaint();
//            camera2.repaint();
            gameView.repaint();
            gameMap.cleanUp();
        try {
            Thread.sleep((long) framePeriod);
            ping = System.currentTimeMillis() - ping;
            framePeriod += (FRAME_PERIOD_MILLIS - ping);
            framePeriod = framePeriod > 0 ? framePeriod : 0;
//            System.out.printf("Frame Rate: %f\n", (float)(1000/((float)framePeriod)));
        } catch (InterruptedException ex) {
            System.out.print(ex);
        }
      }
    }
    
    Dimension offset = new Dimension(520,0);
    public void paintEverthing(Graphics2D g2d){
        camera1.drawView(g2d,new Dimension(0,0));
        camera2.drawView(g2d,offset);
        miniMap.draw(g2d,screenSplitLocation);
    }
    
    public static BufferedImage getSprite(String path)
    {
        BufferedImage img = null;
        URL url = Game.class.getResource(path);
        try {
            BufferedImage paintable = ImageIO.read(url);
            img = Game.getCompatImage(img,paintable.getWidth(),paintable.getHeight());
            Graphics2D g2d = img.createGraphics();
            g2d.drawImage(paintable,0,0, null);
            g2d.dispose();
//            System.out.print(url);
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
    
    public static BufferedImage getCompatImage(BufferedImage img,int wd, int ht){
        img = config.createCompatibleImage(wd, ht, Transparency.TRANSLUCENT);
        img.setAccelerationPriority(1);
        return img;
    }
    
    private class GamePanel extends JPanel{
        public GamePanel(Dimension size){
            super();
            setSize(size);
        }
        
        @Override
        public void paint(Graphics g){
            super.paint(g);
            Graphics2D g2d = (Graphics2D)g;
            paintEverthing(g2d);
            
        }
        
    }
    
    public static void playClip(String path){
        try {
            URL defaultSound = Game.class.getResource(path);
//            System.out.print("\n"+defaultSound.toString());
            // getClass().getSy.getResource("/images/ads/WindowsNavigationStart.wav");
//            File soundFile = new File(defaultSound.toURI());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(defaultSound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.out.print("\n"+e.toString());
        }
    }

}

