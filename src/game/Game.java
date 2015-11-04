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
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import map.GameObject;
import map.Map;
import map.Tile;

/**
 *
 * @author awmil_000
 */
public class Game extends JFrame implements Runnable{
    
    private Thread thread;
    
    BufferedImage bimg;
    Map gameMap;
    MapView camera;
    static final int TILES_PER_DIMENSION = 20;
    static final Dimension SCREENSIZE = new Dimension(20,20);
    
    public Game(int sqrtMapTiles){   
        //gameMap stores persistant world data
        //constructor creates a square map with the given side length
        gameMap = new Map(SCREENSIZE);
        
        GameObject gob1 = new GameObject(
                (BufferedImage) getSprite("res/island1.png"), 
                new Dimension(40,40)
        );
        gameMap.add(gob1);
        
        bimg = (BufferedImage) getSprite("res/island2.png");
        
        GameObject gob2 = new GameObject( bimg,
                new Dimension(20,20)
        );
        gameMap.add(gob2);
        
        //camera is a view into the gameMap
        //currently this should show the whole map
        camera = new MapView(gameMap);
                
        add(camera);
        setSize(camera.getDimens());

        setTitle("Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(new KeyController());
    }
    
    
    public static void main(String args[]) {
        JFrame runner = new Game(TILES_PER_DIMENSION);
        runner.setVisible(true); 
    }

    @Override
    public void run() {
    }
    
    public Image getSprite(String url)
    {
        Image img = new BufferedImage(40,40,BufferedImage.TYPE_INT_RGB);
        try {
            img = ImageIO.read(new File(url));
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }

}
