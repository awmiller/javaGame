/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author awmil_000
 */
public class Map extends Observable implements Controller{
    
    ArrayList<GameObject> contents;
    Dimension corner;

    public Dimension getCorner() {
        return new Dimension(corner.width+Tiles.STANDARD_WIDTH,corner.height+Tiles.STANDARD_HEIGHT);
    }
    Coordinates[] subview = new Coordinates[2];
    ArrayList<Tile> tiles;
    Graphics2D MapGraphic;
    BufferedImage bimg;
    
    public Map(Dimension d){
        tiles = new ArrayList();
        int area = d.height*d.width;
        corner = new Dimension(d.width*Tiles.STANDARD_WIDTH,d.height*Tiles.STANDARD_HEIGHT);
        for(int i = 0; i < area;i++){
            tiles.add(Tiles.getRandom());
        }   
        contents = new ArrayList<>();
    }
    
    
    public GameObject add(GameObject go){
        contents.add(go);
        addObserver(go);
        return go;
    }
    
    public Graphics drawWorld(Graphics g){
        int i =0;
        for(int y = 0;y<corner.height;y+=Tiles.STANDARD_HEIGHT){
            for(int x = 0;x<corner.width;x+=Tiles.STANDARD_WIDTH){
                ((Graphics2D)g).drawImage(tiles.get(i++).img, null, x, y);
            }
        }
        
        return g;
    }

    @Override
    public void recieveKeyCode(KeyEvent ke) {
       
    }

    @Override
    public void recieveGameEvent(GameEvent ge) {
       
    }

    public final ArrayList<GameObject> getObjects(){
        return contents;
    }
}
