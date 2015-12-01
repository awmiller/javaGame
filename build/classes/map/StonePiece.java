/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import game.MoveEvent;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author awmil_000
 */
class StonePiece extends GamePiece {

    private int ClipCooldown=0;
    private Map mContainer;
    private static BufferedImage StoneImg = Game.getCompatSprite("/res/kbr8/Rock.png");
    public StonePiece(Dimension dimension, Map container) {
        super(StoneImg,dimension);
        rigid = true;
        mContainer = container;
    }

    @Override
    public void move() {
        if(ClipCooldown>0) ClipCooldown--;
    }
    
    

    @Override
    public void onCollide(GamePiece collider) {
        if((collider instanceof CharacterPiece)&&this.rigid){
            Dimension oldLocation = this.Location;
            this.rigid = false;
            collider.rigid = false;
            mContainer.movePieceIfAble(this, new MoveEvent(collider.getMove()));
            this.rigid = true;
            collider.rigid = true;
            if(!Location.equals(oldLocation)){
                
                Dimension nextLoc = Game.add(collider.Location, collider.NextMove);
                if((nextLoc.width%40>2)||(nextLoc.height%40>2))
                        collider.Location = nextLoc;
                 if(ClipCooldown ==0){
                     Game.playClip("/res/kbr8/Rock.wav");
                     ClipCooldown = 40;
                 }
            }
        }
    }
    
    
}
