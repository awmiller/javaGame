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
 * @author Andros
 */
class TnTPiece extends GamePiece {

    private static BufferedImage image = Game.getCompatSprite("/res/kbr8/TNT.png");
    public TnTPiece(Dimension dimension) {
        super(image,dimension);
        rigid = true;
    }

    @Override
    public void onDispose() {
        super.onDispose(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onCollide(GamePiece collider) {
        dispose = true;
        collider.dispose = true;
    }

    @Override
    public boolean drawsBefore(GamePiece obj) {
        return false;
    }
    
    
    
}
