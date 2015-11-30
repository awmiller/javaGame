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
class SawPiece extends GamePiece {

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    int speed = 1;
    int type = 0;
    
    private static final BufferedImage VerticalSaw = Game.getCompatSprite("/res/kbr8/Saw_vertical.gif");
    private static final BufferedImage HorizSaw = Game.getCompatSprite("/res/kbr8/Saw_horizontal.gif");
    public SawPiece(Dimension dimension, int i) {
        super(getImage(i),dimension);
        type =i;
        if(type == HORIZONTAL){
            NextMove = new Dimension(speed,0);
        }else if(type == VERTICAL){
            NextMove = new Dimension(0,speed);
        }
        rigid = true;
    }
    
    private static BufferedImage getImage(int i){
        switch(i){
            case HORIZONTAL:
                return HorizSaw;
            default:
                return VerticalSaw;
        }
        
        
    }

    @Override
    public void onCollide(GamePiece collider) {
        if(collider instanceof CharacterPiece){
            collider.dispose = true;
        }
        else{
            speed = -speed;
            if(type == HORIZONTAL){
                NextMove = new Dimension(speed,0);
            }else if(type == VERTICAL){
                NextMove = new Dimension(0,speed);
            }
        }       
    }

    @Override
    public void move() {
        setChanged();
        notifyObservers();
        clearChanged();
    }
    
    
    
}
