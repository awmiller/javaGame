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
    
    public static final BufferedImage missleImg = Game.getSprite("/res/Rocket.gif");
    public static final BufferedImage bulletImg = Game.getSprite("/res/Shell.png");
    public static final BufferedImage bouncerImg = Game.getSprite("/res/Bouncing.gif");
    
    protected boolean canRicochet = false;

    
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
        }else {
            pp = new ProjectilePiece(bulletImg,origin);
            pp.Power = 5;
        }
        
        pp.Heading = heading - Math.PI;
        return pp;
    }
    
    @Override
    public void move() {
        NextMove = Game.rotate(12, Heading);
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
        super.dispose = true;
        if(collider instanceof CharacterPiece){
            ((CharacterPiece)collider).takesDamage(Power);
        }
    }    
    
}
