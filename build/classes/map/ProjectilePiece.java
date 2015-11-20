/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
import game.Game;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author Andros
 */
class ProjectilePiece extends GamePiece {

    private double Heading;
    private int Power;
    
    Dimension lastPlace;
    
    public static final BufferedImage missleImg = Game.getSprite("/res/Rocket.png");
    public static final BufferedImage bulletImg = Game.getSprite("/res/Shell.png");
    public static final BufferedImage bouncerImg = Game.getSprite("/res/Bouncing.png");
    public static final BufferedImage SbouncerImg = Game.getSprite("/res/SuperBouncing.png");
    
    protected boolean canRicochet = false;
    protected int CollisionLife =0;

    
    ProjectilePiece(double heading, int power, Dimension location) {
        super(Game.getSprite("/res/Shell.png"),location);
        Heading = heading - Math.PI;
        Power = power;
        lastPlace = location;
        lastHeading = Heading;
    }

        
    private ProjectilePiece(BufferedImage img, Dimension location) {
        super(img,location);
        lastPlace = location;
        
        lastHeading = Heading;
    }
    
    public static ProjectilePiece getProjectile(AttackEvent ae, Dimension origin, double heading){
        ProjectilePiece pp;
        if(ae == AttackEvent.MissileAttack){
            pp = new ProjectilePiece(missleImg,origin);
            pp.Power = 10;
        }else if(ae==AttackEvent.BouncingAttack){
            pp = new ProjectilePiece(bouncerImg,origin);  
            pp.Power = 10;
            pp.canRicochet = true;
            pp.CollisionLife = 3;
        }else if(ae==AttackEvent.SuperBouncingAttack){
            pp = new ProjectilePiece(SbouncerImg,origin);  
            pp.Power = 30;
            pp.canRicochet = true;
            pp.CollisionLife = 10000;
        }else {
            pp = new ProjectilePiece(bulletImg,origin);
            pp.Power = 5;
        }
        
        pp.speed+=ae.attackSpeed;
        
        pp.Heading = heading - Math.PI;
        return pp;
    }
    
    int speed = 12;
    
    @Override
    public void move() {
//        if(CollisionLife>10) Heading = Math.abs(Heading);
        NextMove = Game.rotate(speed, Heading);
        setChanged();
        notifyObservers(AttackEvent.MissileAttack);
        clearChanged();
    }

    @Override
    public void draw(Graphics2D g2d) {
        
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(Heading- Math.PI,size.width/2,size.height/2);
        AffineTransformOp atxop = new AffineTransformOp(rotation,AffineTransformOp.TYPE_BILINEAR);
        g2d.drawImage(super.Image, atxop, Location.width-size.width/2, Location.height-size.height/2);
    }

    @Override
    public boolean drawsBefore(GamePiece obj) {
        return true;
    }

    double lastHeading;
    @Override
    public void onCollide(GamePiece collider) {
        super.onCollide(collider);
        
        if(collider.rigid == false) return;
        
        if(CollisionLife==0){
            super.dispose = true;
        }else{
            CollisionLife--;
        }
            
        if(collider instanceof CharacterPiece){
            ((CharacterPiece)collider).takesDamage(Power);
            super.dispose = true;
        }else if(canRicochet){
            double h = Math.abs(Heading)%(2*Math.PI);
            if(h<0) h+=Math.PI*2;
            int right = this.Location.width - collider.Location.width;
            int top = this.Location.height - collider.Location.height;
            
            
            System.out.printf("\nHeading %f r %d t %d ",h,right,top);
            double apply =0;
            if(Math.abs(right) > Math.abs(top)){
                if(h<Math.PI/2)
                    apply-=Math.PI/8;
                else if(h<Math.PI)
                    apply+=Math.PI/8;
                else if(h<3*Math.PI/2)
                    apply-=Math.PI/8;
                else
                    apply+=Math.PI/8;
                
            }else{
                if(h<Math.PI/2)
                    apply+=Math.PI/8;
                else if(h<Math.PI)
                    apply-=Math.PI/8;
                else if(h<3*Math.PI/2)
                    apply+=Math.PI/8;
                else
                    apply-=Math.PI/8;    
            }
            
            Heading+=apply;
            NextMove = Game.rotate(speed, Heading);
            this.Location = Game.add(Location, NextMove);
            Dimension save = Location;
            while(collider.isColliding(this) || this.isColliding(collider)){
                this.Location = save;
                Heading+=apply;
                NextMove = Game.rotate(speed, Heading);
                this.Location = Game.add(Location, NextMove);
            }
        }
        
    }    
    
}
