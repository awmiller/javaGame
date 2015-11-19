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

/**
 *
 * @author Andros
 */
class ProjectilePiece extends GamePiece {

    private double Heading;
    private int Power;
    
    ProjectilePiece(double heading, int power, Dimension location) {
        super(Game.getSprite("/res/Shell.png"),location);
        Heading = heading - Math.PI;
        Power = power;
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
