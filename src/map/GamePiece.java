/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;
import game.Game;

/**
 *
 * @author awmil_000
 */
public abstract class GamePiece extends Observable{
    
    protected boolean rigid = false;
    
    protected BufferedImage Image;
    protected Dimension Location;
    public Dimension getLocation() {
        return Location;
    }
    
    protected Dimension size;
    public Dimension getSize() {
        return size;
    }
    
    protected boolean dispose;
    public boolean disposable(){return dispose;}
    
    public GamePiece(BufferedImage image,Dimension location){
        Image = image;
        Location = location;
        dispose = false;
        size = new Dimension(Image.getHeight(),Image.getWidth());
    }
    
    public boolean isColliding(GamePiece other){
        //is distance between < 2R
        //subclasses should override for better behavior
        return false;
    }

    public boolean drawsBefore(GamePiece obj) {
        return false;//by default draw in order
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(Image,Location.width, Location.height,(ImageObserver)null);
    }

    boolean isRigid() {
        return rigid;
    }

    boolean isColliding(Rectangle R1) {
        Rectangle thisbox = Game.getRectCollider(this.Location, this.size);
        return thisbox.intersects(R1);
    }
    
    public interface PieceMapInterface{
        
    }

}
