/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
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
    
    private double RADIANS_PER_FRAME = Math.PI/(1*Game.FRAMES_PER_SECOND);
    
    private int maxHealth = 40;
    
    protected int Health;
    public int getHealth() {
        return Health;
    }

    protected int Power = 5;
    public int getPower() {
        return Power;
    }

    protected int Armor;
    public void setArmor(int level){
        Armor =level;
    }
    
    private int speed;
    private double heading;
    public final double getHeading(){return heading;}
    
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
        speed = 6;
        this.rigid =true;
        Health = maxHealth;
    }

    @Override
    public void draw(Graphics2D g2d) {

        AffineTransformOp atxop = new AffineTransformOp(rotation,AffineTransformOp.TYPE_BILINEAR);

        g2d.drawImage(super.Image, atxop, Location.width-size.width/2, Location.height-size.height/2);

        float percent = Health/maxHealth;
        int red = Math.round(255-(percent * 255));
        int green = Math.round(percent*255);//
        g2d.setColor(new Color(red,green,0,255));
        g2d.fillRect(Location.width-size.width/2, Location.height+size.height/2,Health,10);
//        g2d.draw(new Rectangle(Location.width-size.width/2, Location.height+size.height/2,Health,20));
//        g2d.drawOval(Location.width-radius(), Location.height-radius(), 2*radius(),2*radius());

    }
    
    
 
    
    public AffineTransform rotation = new AffineTransform();
    private KeyController mControlls;
    private KeyController.ControlModelInterface mcmi = new KeyController.ControlModelInterface() {
        

        @Override
        public void onEvent() {
            
        }
    };
    
    int FrameCount = 0;
    int AttackCooldown = 0;

    @Override
    public void move() {
        FrameCount++;
        hasMoved = true;
        NextMove = Game.ZERO_VECTOR;
        int move = 0;
        
        if(AttackCooldown >0){
            AttackCooldown--;
        }

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
        
        if(mControlls.eventQueue.contains(AttackEvent.MissileAttack)){
            
            System.out.printf("\nATTACK COOLDOWN %d :: Frame %d", AttackCooldown,FrameCount);
            mControlls.eventQueue.remove(AttackEvent.MissileAttack);
            if(AttackCooldown ==0){
                AttackCooldown = Game.FRAMES_PER_SECOND;
                setChanged();
                notifyObservers(AttackEvent.MissileAttack);
                clearChanged();
                return;
            }
        }

        notifyObservers();
        clearChanged();
    }

}
