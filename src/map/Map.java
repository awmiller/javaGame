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
    }
    
    
    public GamePiece add(GamePiece go){
        contents.add(go);
//        go.addObserver((observerthis);
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
        GameEvent gpe = (GameEvent)arg;
        switch(gpe.type){
            case GameEvent.MOVE_THIS_PIECE:
                movePieceIfAble((GamePiece)o,(MoveEvent)arg);
                break;
            case GameEvent.ATTACK_EVENT:
                attackIfAble((GamePiece)o,(AttackEvent)arg);
        }
        
    }

    public void movePieceIfAble(GamePiece gamePiece, MoveEvent moveEvent) {        
        Dimension newLocation = Game.add(gamePiece.Location,moveEvent.vector);
        Rectangle R1 = Game.getRectCollider(newLocation,gamePiece.size);
        for(GamePiece other : contents){
            if(other.isRigid() && (other!=gamePiece)){
                if(other.isColliding(R1)){//check in if new position is colliding
                    if(!other.isColliding(gamePiece))//disallow move if previous position is ok
                        return;
                }
            }
        }
        if(!movesOutOfBounds(gamePiece,newLocation))
        gamePiece.Location = newLocation;
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
}
