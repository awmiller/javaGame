/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import game.KeyController;
import game.MoveEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Observable;


public class CharacterPiece extends GamePiece {
    
    protected int Health;
    private double RADIANS_PER_FRAME = Math.PI/(1*Game.FRAMES_PER_SECOND);
    
    public int getHealth() {
        return Health;
    }

    protected int Power;
    public int getPower() {
        return Power;
    }

    protected int Armor;
    public void setArmor(int level){
        Armor =level;
    }
    
    private int speed;
    private double heading;
    
    public int takesDamage(int power){
        int damage = (power-Armor)>0?(power-Armor):0;
        Health -= damage;
        return damage;
    }
    
    @Override
    public boolean drawsBefore(GamePiece obj) {
        return 
        (this.Location.height>obj.Location.height) || 
                ((this.Location.width < obj.Location.width)&&
                    (this.Location.height == obj.Location.height));
    }     

    public CharacterPiece(BufferedImage image,KeyController kc, Dimension location) {
        super(image, location);
        mControlls = kc;
        mControlls.attach(mcmi);
        speed = 3;
        this.rigid =true;
    }

    @Override
    public void draw(Graphics2D g2d) {
        //g2d.setColor(Color.BLUE);
        //g2d.drawOval(Location.width-radius()/2, Location.height-radius()/2, radius(),radius());
        
//        move();
        if(hasMoved){
        
        AffineTransformOp atxop = new AffineTransformOp(rotation,AffineTransformOp.TYPE_BILINEAR);
//        move.translate(Location.width, Location.height);
        g2d.drawImage(super.Image, atxop, Location.width-size.width/2, Location.height-size.height/2);
        g2d.setColor(Color.RED);
        g2d.drawOval(Location.width-radius(), Location.height-radius(), 2*radius(),2*radius());
        hasMoved = false;
        }
    }
    
    
 
    public Dimension NextMove = Game.ZERO_VECTOR;
    public AffineTransform rotation = new AffineTransform();
    private KeyController mControlls;
    private KeyController.ControlModelInterface mcmi = new KeyController.ControlModelInterface() {
        

        @Override
        public void onEvent() {
            
        }
    };
    
    int FrameCount = 0;

    @Override
    public void move() {

        hasMoved = true;
        NextMove = Game.ZERO_VECTOR;
        int move = 0;

        if (mControlls.eventQueue.contains(MoveEvent.RotateRight)) {
            setChanged();
            heading += RADIANS_PER_FRAME;
            rotation.rotate(RADIANS_PER_FRAME,size.width/2,size.height/2);

        }
        if (mControlls.eventQueue.contains(MoveEvent.RotateLeft)) {
            setChanged();
            heading -= RADIANS_PER_FRAME;
            rotation.rotate(-RADIANS_PER_FRAME,size.width/2,size.height/2);
        }

        if (mControlls.eventQueue.contains(MoveEvent.MoveUp)) {
            setChanged();
            move -= speed;
        }
        if (mControlls.eventQueue.contains(MoveEvent.MoveDown)) {
            setChanged();
            move += speed;
        }

        NextMove = Game.rotate(move, heading);

        notifyObservers();
        clearChanged();
    }

}
