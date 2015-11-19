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

    public static final int POWER_BOUNCE = 0;
    public static final int POWER_MISSILE = 1;
    public static final int POWER_SHIELD = 2;
    public static final int POWER_TURRET = 3;
    
    public static BufferedImage POWER_BOUNCE_IMG = Game.getSprite("/res/PickupB.png");
    public static BufferedImage POWER_MISSILE_IMG = Game.getSprite("/res/PickupM.png");
    public static BufferedImage POWER_SHIELD_IMG = Game.getSprite("/res/PickupS.png");
    public static BufferedImage POWER_TURRET_IMG = Game.getSprite("/res/PickupT.png");

    static GamePiece getRandom(Dimension rem) {
        int t = Math.round((float)Math.random()*3);
        switch(t){
            case POWER_BOUNCE:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                        rem,PowerUpPiece.POWER_BOUNCE);
            case POWER_MISSILE:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                        rem,PowerUpPiece.POWER_MISSILE);
            case POWER_SHIELD:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                        rem,PowerUpPiece.POWER_SHIELD);
            default:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                        rem,PowerUpPiece.POWER_TURRET);
        }
    }
    
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
