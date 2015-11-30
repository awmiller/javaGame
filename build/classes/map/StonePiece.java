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

    private Map mContainer;
    private static BufferedImage StoneImg = Game.getCompatSprite("/res/kbr8/Rock.gif");
    public StonePiece(Dimension dimension, Map container) {
        super(StoneImg,dimension);
        rigid = true;
        mContainer = container;
    }

    @Override
    public void onCollide(GamePiece collider) {
        if(collider instanceof CharacterPiece){
            Dimension oldLocation = this.Location;
            mContainer.movePieceIfAble(this, new MoveEvent(collider.getMove()));
            if(!Location.equals(oldLocation)){
                 collider.Location = Game.add(collider.Location, collider.NextMove);
            }
        }
    }
    
    
}
