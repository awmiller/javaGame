/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class ObstaclePiece extends GamePiece {
    
    boolean Destructable = false;
    private int Health = 4;

    public ObstaclePiece(BufferedImage image, Dimension location) {
        super(image, location);
        rigid = true;
    }   

    ObstaclePiece(BufferedImage image, Dimension location, boolean b) {
        super(image, location);
        rigid = true;
        Destructable = b;
    }

    @Override
    public boolean isColliding(GamePiece other) {
        Rectangle r1 = Game.getRectCollider(Location, size);
        return other.isColliding(r1);
    }
    
    

    @Override
    public void onCollide(GamePiece collider) {
        if((collider instanceof ProjectilePiece)&&Destructable){
            Health--;
        }
        if(Health < 1){
            dispose = true;
        }
    }
    
    
}
