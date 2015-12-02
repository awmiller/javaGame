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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


public class CharacterPiece extends GamePiece {
    
    protected Dimension topLeft(){
        return new Dimension(Location.width-size.width/2,Location.height-size.height/2);
    }
    
    private int speed;
    
    public int setSpeed(int newSpeed){
        speed = newSpeed;
        return speed;
    }
    public int getSpeed() {return speed;}
    
    protected int maxHealth = 60;
    
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
    
    private int Score=0;
    @Override
    public int addScore(int i) {
        Score += i;
        return Score;
    }
    public final int getScore(){return Score;}
    
    public int takesDamage(int power){
        int damage = (power-Armor)>0?(power-Armor):0;
        Health -= damage;
        if(Health <=0) {
            dispose =true;
            this.Respawn = 2*Game.FRAMES_PER_SECOND;
        }
        if(Armor>0){Armor-=damage;}
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
        speed = 6;
        this.rigid =true;
        Health = maxHealth;
    }

    @Override
    public void draw(Graphics2D g2d) {
//        mFrameAnimator.update(AnmState);
//        
//        g2d.drawImage(mFrameAnimator.getFrame(), Location.width-size.width/2, Location.height-size.height/2,null);
//        
        
//        float percent = (float)Health/(float)maxHealth;
//        int red = Math.round(255 - (float)Math.pow(255, percent));
//        int green = Math.round((float)Math.pow(255, percent));//
        g2d.setColor(Color.RED);
//        g2d.fillRect(Location.width-size.width/2, Location.height+size.height/2,Health,5);
        g2d.drawRect(Location.width-size.width/2, Location.height-size.height/2, size.width, size.height);
    }

    public KeyController mControlls;
    
    protected int FrameCount = 0;

    @Override
    public void move() {
        hasMoved = true;
    }
    
    @Override
    public void onRespawn(){
        super.onRespawn();
        Health = maxHealth;
    }

    @Override
    boolean isColliding(Rectangle R1) {
        Shape s = new Rectangle(Location.width-size.width/2,Location.height-size.height/2,
                size.width,size.height);
//        Ellipse2D.Double(Location.width-size.width/2+epsillon/2,
//                Location.height-size.height/2+epsillon/2,
//                size.width-epsillon,size.height-epsillon);
        return s.intersects(R1);
    }
    
    

}
