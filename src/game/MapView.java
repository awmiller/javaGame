/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import map.GamePiece;
import map.Map;

/**
 * TODO: this should draw to a buffered image instead of panel so subviews may be drawn
 * TODO: implement minimap with getScaledInstance()
 * TODO: do all work in @Overide paint()
 * @author awmil_000
 */
public class MapView extends JPanel {

    private Map map;
    private Dimension dimens;
    private BufferedImage world;
    private GamePiece player;
    private Color transparent = new Color(255, 255, 255, 0);

    public Dimension getDimens() {
        return dimens;
    }

    long ping =System.nanoTime();
        public Graphics2D drawView(Graphics g) {
                    
        Graphics2D g2d = (Graphics2D) g;
       
        Dimension ScreenClip = getRelativeScreenLocation();
        
        BufferedImage scale = 
                map.getBackgroundImage().getSubimage(
                        ScreenClip.width, 
                        ScreenClip.height, 
                        dimens.width,dimens.height);
        
        g2d.drawImage(scale,0,0,null);  
        
         BufferedImage scale2 = 
                map.printObjectsImage().getSubimage(
                        ScreenClip.width, 
                        ScreenClip.height, 
                        dimens.width,dimens.height);

        g2d.drawImage(scale2,0,0,null); 
        
        return g2d;
    }
        
    public Graphics2D drawView(Graphics g, Dimension offset) {
                    
        Graphics2D g2d = (Graphics2D) g;
       
        Dimension ScreenClip = getRelativeScreenLocation();
        
         BufferedImage scale2 = 
                map.printObjectsImage().getSubimage(
                        ScreenClip.width, 
                        ScreenClip.height, 
                        dimens.width,dimens.height);

        g2d.drawImage(scale2,offset.width,offset.height,null); 
        
        return g2d;
    }

    public MapView(Map m, Dimension d, GamePiece snap) {
        super();
        map = m;
        dimens = d;
        setSize(dimens);
        player = snap;
        world = Game.getCompatImage(world, map.getCorner().width, map.getCorner().height);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawView(g);
    }

    private Dimension getRelativeScreenLocation() {
        int ScreenX =player.getLocation().width - (dimens.width/2);
        int ScreenY =player.getLocation().height- (dimens.height/2);
        int boundx = (map.getCorner().width-dimens.width);
        int boundy = (map.getCorner().height-dimens.height);
        
        if(ScreenX < 1){
            ScreenX = 0;
        }
        if(ScreenY < 1){
            ScreenY = 0;
        }
        if(ScreenX > boundx){
            ScreenX = boundx;
        }
        if(ScreenY > boundy){
            ScreenY = boundy;
        }
        return new Dimension(ScreenX,ScreenY);
    }
    
    
}
