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
    public static final int POWER_FASTFORWARD = 4;
    public static final int POWER_SUPERBOUNCE = 5;
    
    public static BufferedImage POWER_BOUNCE_IMG = Game.getSprite("/res/PickupB.png");
    public static BufferedImage POWER_MISSILE_IMG = Game.getSprite("/res/PickupM.png");
    public static BufferedImage POWER_SHIELD_IMG = Game.getSprite("/res/PickupS.png");
    public static BufferedImage POWER_TURRET_IMG = Game.getSprite("/res/PickupT.png");
    public static BufferedImage POWER_FASTFORWARD_IMG = Game.getSprite("/res/ic_media_ff.png");
    public static BufferedImage POWER_SUPERBOUNCE_IMG = Game.getSprite("/res/SuperBouncing.png");

    static GamePiece getRandom(Dimension rem) {
        int t = Math.round((float)Math.random()*16);
        switch(t){
            case 6:
            case 7:
            case 8:
            case POWER_BOUNCE:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                        rem,PowerUpPiece.POWER_BOUNCE);
            case 9:
            case 10:
            case 11:
            case POWER_MISSILE:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_TURRET_IMG, 
                        rem,PowerUpPiece.POWER_TURRET);
            case 12:
            case 13:
            case POWER_SHIELD:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_SHIELD_IMG, 
                        rem,PowerUpPiece.POWER_SHIELD);
            
            case 14:
            case POWER_FASTFORWARD:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_FASTFORWARD_IMG, 
                        rem,PowerUpPiece.POWER_FASTFORWARD);
            
            case POWER_SUPERBOUNCE://increasing chances of missle and bounce
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_SUPERBOUNCE_IMG, 
                        rem,PowerUpPiece.POWER_SUPERBOUNCE);
            default:
                return 
                    new PowerUpPiece(PowerUpPiece.POWER_MISSILE_IMG, 
                        rem,PowerUpPiece.POWER_MISSILE);
                
        }
    }
    
    private int puType;
    public PowerUpPiece(BufferedImage img, Dimension dimension, int type) {
        super(img,dimension);
        puType = type;
        rigid = false;
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
