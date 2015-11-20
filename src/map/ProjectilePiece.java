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
    
    public static final BufferedImage missleImg = Game.getSprite("/res/Rocket.png");
    public static final BufferedImage bulletImg = Game.getSprite("/res/Shell.png");
    public static final BufferedImage bouncerImg = Game.getSprite("/res/Bouncing.png");
    
    protected boolean canRicochet = false;
    protected int CollisionLife =0;

    
    ProjectilePiece(double heading, int power, Dimension location) {
        super(Game.getSprite("/res/Shell.png"),location);
        Heading = heading - Math.PI;
        Power = power;
    }

        
    private ProjectilePiece(BufferedImage img, Dimension location) {
        super(img,location);
    }
    
    public static ProjectilePiece getProjectile(AttackEvent ae, Dimension origin, double heading){
        ProjectilePiece pp;
        if(ae == AttackEvent.MissileAttack){
            pp = new ProjectilePiece(missleImg,origin);
            pp.Power = 10;
        }else if(ae==AttackEvent.BouncingAttack){
            pp = new ProjectilePiece(bouncerImg,origin);  
            pp.Power = 5;
            pp.canRicochet = true;
            pp.CollisionLife = 3;
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

    @Override
    public void onCollide(GamePiece collider) {
        super.onCollide(collider);
        
        if(CollisionLife==0){
            super.dispose = true;
        }else{
            CollisionLife--;
        }
            
        if(collider instanceof CharacterPiece){
            ((CharacterPiece)collider).takesDamage(Power);
        }else if(canRicochet){
            double h = Math.abs(Heading)%(2*Math.PI);
            if(h<0) h+=Math.PI*2;
            
//            System.out.printf("\nHeading %f  ",h);
            int right = this.Location.width - collider.Location.width;
            int top = this.Location.height - collider.Location.height;
            
            if(Math.abs(right) > Math.abs(top)){
                if(h<Math.PI/2)
                    Heading-=Math.PI/2;
                else if(h<Math.PI)
                    Heading+=Math.PI/2;
                else if(h<3*Math.PI/2)
                    Heading-=Math.PI/2;
                else
                    Heading+=Math.PI/2;
                
            }else{
                if(h<Math.PI/2)
                    Heading+=Math.PI/2;
                else if(h<Math.PI)
                    Heading-=Math.PI/2;
                else if(h<3*Math.PI/2)
                    Heading+=Math.PI/2;
                else
                    Heading-=Math.PI/2;    
            }
        }
    }    
    
}
