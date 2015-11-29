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
class ExitPiece extends GamePiece{

    private static BufferedImage image = Game.getCompatSprite("/res/kbr8/Exit1.png");
    public ExitPiece(Dimension location) {
        super(image, location);
    }
    
}
