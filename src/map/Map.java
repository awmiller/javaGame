/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
import game.Game;
import game.MapView;
import game.MoveEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author awmil_000
 */
public class Map extends Observable implements Observer{
    
    ArrayList<GamePiece> contents;
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
        bimg = new BufferedImage(corner.width,corner.height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gimg = bimg.createGraphics();
        gimg = (Graphics2D) drawWorld(gimg);
        System.out.printf("Map Size: %d,%d", corner.height, corner.width);
        System.out.printf("Map Image Size: %d,%d", bimg.getHeight(),bimg.getWidth());
        BufferedImage wall = (BufferedImage) Game.getSprite("res/Wall1.gif");
        for(int i =0; i< corner.height;)
        {
            for(int j=0; j < corner.width;)
            {
                if( (i==0) || (j==0) || 
                        (i>(corner.height-wall.getHeight())) ||
                        (j>(corner.width -wall.getWidth() ))
                  ){
                    contents.add(new ObstaclePiece(wall,new Dimension(j,i)));
                }
                j+= wall.getWidth();
            }
            i+=wall.getHeight();
        }
    }
    
    public Image getBackgroundImage(){
        return bimg;
    }
    
    public GamePiece add(GamePiece go){
        contents.add(go);
        go.addObserver(this);
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

    public final ArrayList<GamePiece> getObjects(){
        return contents;
    }

    @Override
    public void update(Observable o, Object arg) {
//        GameEvent gpe = (GameEvent)arg;
//        switch(gpe.type){
//            case GameEvent.MOVE_THIS_PIECE:
//                movePieceIfAble((GamePiece)o,(MoveEvent)arg);
//                break;
//            case GameEvent.ATTACK_EVENT:
//                attackIfAble((GamePiece)o,(AttackEvent)arg);
//        }
        
        CharacterPiece cp = (CharacterPiece) o;
        movePieceIfAble(cp,new MoveEvent(cp.NextMove));
//        this.setChanged();
//        this.notifyObservers();
//        this.clearChanged();
        
    }

    public void movePieceIfAble(GamePiece gamePiece, MoveEvent moveEvent) {        
        Dimension newLocation = Game.add(gamePiece.Location,moveEvent.vector);
        Dimension oldLocation = new Dimension(gamePiece.Location);
        gamePiece.Location = newLocation;
        for(GamePiece other : contents){
            if(other.isRigid() && (other!=gamePiece)){
                if(other.isColliding(gamePiece)){
//                    System.out.print("COLLISION!\n");
                    //if new position is colliding
                    gamePiece.Location = oldLocation;
                    if(other.isColliding(gamePiece)){//disallow move if previous position is ok
                        //if old position is colliding too, prefer new position
//                        System.out.print("STUCK!\n");
                        gamePiece.Location = newLocation;
                    }
                }
            }
        }
        //if(!movesOutOfBounds(gamePiece,newLocation))
        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    private void attackIfAble(GamePiece gamePiece, AttackEvent attackEvent) {
        
    }

    private boolean movesOutOfBounds(GamePiece gamePiece, Dimension newLocation) {
        int top = newLocation.height;
        int bot = newLocation.height + gamePiece.size.height;
        int left = newLocation.width;
        int right = newLocation.width + gamePiece.size.width;
        
        return (top<0)||(bot>corner.height)||(left<0)||(right>corner.width);
    }
    
    public void attachCamera(MapView mv){
        this.addObserver(mv);
    }

    public Graphics2D drawObjects(Graphics2D g2d)  {
        //copy contents so we can modify it            
        ArrayList<GamePiece> drawables = new ArrayList<>(contents);

        //recursively draws everything in rastor scan order
        while (drawables.size() > 0) {
            GamePiece obj = drawables.remove(0);
            recurseDraw(obj, drawables, g2d);
        }
        
        for(GamePiece gp: contents){
            for(GamePiece ogp: contents){
                if(gp!=ogp && (gp.getClass()==CharacterPiece.class) && (ogp.getClass()==CharacterPiece.class)){
                    g2d.drawLine(gp.Location.width, gp.Location.height, ogp.Location.width, ogp.Location.height);
                }
            }
        }

        return g2d;
    }
    
    
    private void recurseDraw(GamePiece obj, ArrayList<GamePiece> drawables, Graphics2D g2d) {
        /**
         * iterate over all the elements and remove elements that would draw
         * first if the element does draw before object, run this algorithm
         * again to make sure that the remaining objects draw in order
         *
         */
        for (int i = 0; i < drawables.size();) {
            if (drawables.get(i).drawsBefore(obj)) {
                GamePiece first = drawables.remove(i);
                recurseDraw(first, drawables, g2d);
            }
            else  i++;
        }
        /**
         * now that drawables does not contain any elemts that draw before obj
         * we can proceed to draw obj
         *
         */
        obj.draw(g2d);

    }
}
