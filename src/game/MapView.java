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
public class MapView extends JPanel implements Observer{

    private Map map;
    private Dimension dimens;
    private BufferedImage world;
    private GamePiece player;
    private Color transparent = new Color(255, 255, 255, 0);

    public Dimension getDimens() {
        return dimens;
    }

//    MapView(Map gameMap) {
//        super();
//        map = gameMap;
//        dimens = gameMap.getCorner();
//        setSize(dimens);
//    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g.create();
//        drawView(g);
//    }
    long ping =System.nanoTime();
    public Graphics2D drawView(Graphics g) {
                    
        Graphics2D g2d = (Graphics2D) g;
        
        
        
        Dimension ScreenClip = getRelativeScreenLocation();
        
//        int ScreenX =player.getLocation().width - (dimens.width/2);
//        int ScreenY =player.getLocation().height- (dimens.height/2);
//        Dimension ScreenLoc = new Dimension(ScreenX,ScreenY);
        
        Graphics2D world2d = world.createGraphics();
        
        world2d.setBackground(transparent);
        world2d.clearRect(ScreenClip.width, ScreenClip.height, dimens.width, dimens.height);
        
//        g2d.drawImage(map.printImage(), 0, 0, null);
//        world = map.printImage();
        
//        world = map.getBackgroundImage();
        
//        Graphics2D world2d = world.createGraphics();
        
//        map.drawWorld(world2d);
        
//        world2d.drawImage(map.getBackgroundImage(),0,0,null);
        
        
        
//        long t = System.nanoTime();
//        float d1 = ((float)(t -ping))/1000000;
//        
//        map.drawObjects(world2d);
//        world2d.dispose();
        try{
        BufferedImage scale = 
                map.getBackgroundImage().getSubimage(
                        ScreenClip.width, 
                        ScreenClip.height, 
                        dimens.width,dimens.height);

//        t = System.nanoTime();
//        float d2 = ((float)(t-ping))/1000000-d1;               
        
//        g2d.drawImage(map.getBackgroundImage(),0,0,null);
        
//        map.drawObjects(g2d);
        g2d.drawImage(scale,0,0,null);     
                }catch(Exception e){
         System.out.print(e.getMessage());
         throw(e);
        }
//        t = System.nanoTime();
//        float d3 = ((float)(t-ping))/1000000 -d2;
        
        map.drawObjects(world2d,ScreenClip,dimens);
        world2d.dispose();
                try{
         BufferedImage scale2 = 
                world.getSubimage(
                        ScreenClip.width, 
                        ScreenClip.height, 
                        dimens.width,dimens.height);
        
        
        
//        t = System.nanoTime();
//        float d4 = ((float)(t-ping))/1000000-d3;
        
        
        g2d.drawImage(scale2,0,0,null); 
                        }catch(Exception e){
         System.out.print(e.getMessage());
         
         throw(e);
        }
//        System.out.printf(
//                "\nFrame Rate %fHz\nD1-%1.2fus\nD2-%1.2fus\nD3-%1.2fus\nD4-%1.2fus",
//                1000000000/((float)(t - ping)),
//                d1,d2,d3,d4
//                );
//            ping = t;
        
        return g2d;
    }

    public MapView(Map m, Dimension d, GamePiece snap) {
        super();
        map = m;
        dimens = d;
//        dimens = map.getCorner();
        setSize(dimens);
        player = snap;
        world = Game.getCompatImage(world, map.getCorner().width, map.getCorner().height);
//        world =new BufferedImage(map.getCorner().width,map.getCorner().height,BufferedImage.TYPE_INT_ARGB);
    }

//    public Graphics2D drawObjects(Graphics2D g2d) {
//        //copy contents so we can modify it            
//        ArrayList<GamePiece> drawables = new ArrayList<>(map.getObjects());
//
//        //recursively draws everything in rastor scan order
//        while (drawables.size() > 0) {
//            GamePiece obj = drawables.remove(0);
//            recurseDraw(obj, drawables, g2d);
//        }
//
//        return g2d;
//    }
//
//    private void recurseDraw(GamePiece obj, ArrayList<GamePiece> drawables, Graphics2D g2d) {
//        /**
//         * iterate over all the elements and remove elements that would draw
//         * first if the element does draw before object, run this algorithm
//         * again to make sure that the remaining objects draw in order
//         *
//         */
//        for (int i = 0; i < drawables.size();) {
//            if (drawables.get(i).drawsBefore(obj)) {
//                GamePiece first = drawables.remove(i);
//                recurseDraw(first, drawables, g2d);
//            }
//            else  i++;
//        }
//        /**
//         * now that drawables does not contain any elemts that draw before obj
//         * we can proceed to draw obj
//         *
//         */
//        obj.draw(g2d);
//
//    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        Graphics2D g2d = (Graphics2D) g.create();
        drawView(g);
//        world = new BufferedImage(map.getCorner().width,map.getCorner().height,BufferedImage.TYPE_INT_ARGB);
//        this.paintAll(world.createGraphics());
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
