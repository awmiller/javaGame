/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author awmil_000
 */
class PowerUpPiece extends GamePiece {

    public static final int POWER_BOUNCE = 1;
    
    public static BufferedImage POWER_BOUNCE_IMG = Game.getSprite("/res/PickupB.png");
    
    private int puType;
    public PowerUpPiece(BufferedImage img, Dimension dimension, int type) {
        super(img,dimension);
        puType = type;
    }

    int getType() {
        return puType;
    }

    @Override
    public void onCollide(GamePiece collider) {
        if(collider instanceof CharacterPiece)
            dispose = true;
    }

    @Override
    public void move() {
        NextMove = Game.ZERO_VECTOR;
        setChanged();
        notifyObservers();
    }
    
    
    
    
}
