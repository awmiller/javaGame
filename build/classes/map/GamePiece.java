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
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;

/**
 *
 * @author awmil_000
 */
public abstract class GamePiece extends Observable{
    
    protected boolean rigid = false;
    protected boolean nonRigidCollisions = false;
    protected boolean hasMoved = false;
    public Dimension NextMove = Game.ZERO_VECTOR;
    protected int Respawn =0;
    
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
    
    public boolean isColliding(GamePiece other) {
        return isColliding(Game.getRectCollider(other.Location, other.size));
//        int distance = Game.distance(this.Location,other.Location);
//        return distance<(this.collideRadius()+other.collideRadius());
    }

    public boolean drawsBefore(GamePiece obj) {
        return false;//by default draw in order
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(Image,Location.width-size.width/2, Location.height-size.height/2,(ImageObserver)null);
//        g2d.setColor(Color.RED);
//        g2d.drawRect(Location.width-size.width/2, Location.height-size.height/2, size.width, size.height);
    }

    boolean isRigid() {
        return rigid;
    }

    boolean isColliding(Rectangle R1) {
        Rectangle thisbox = Game.getRectCollider(this.Location, this.size);
        return thisbox.intersects(R1);// || R1.contains(thisbox) || thisbox.contains(R1);
    }

    int radius() {
        return size.height/2;
    }    
    
    public void move() {
       
    }
    
    public void onCollide(GamePiece collider){
        //System.out.print("\n"+this.toString()+"Collided with " +collider.toString());
    }
    
    public void onDispose(){
        
    }

    public int getRespawn() {
        if(Respawn > 0){
            Respawn--;
            onRespawn();
        }
        return Respawn;
    }

    public void onRespawn() {
        dispose = false;
    }

    
    public int addScore(int i){return 0;}
    
//    public Shape getCollider(){
//        Dimension offset = new Dimension(Location.width-size.width/2,
//                Location.height-size.height/2);
//        return Game.getRectCollider(offset, size);
//    }

    public int collideRadius() {
        return radius();
    }

    public Dimension getMove() {
        return NextMove;
    }
    
    public void onStartGame(){
        
    }
}
