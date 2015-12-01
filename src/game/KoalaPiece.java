/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.KeyController;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import map.CharacterPiece;
import map.GamePiece;

/**
 *
 * @author awmil_000
 */
class KoalaPiece extends CharacterPiece {

    public KoalaPiece(BufferedImage KoalaImg, KeyController playerController, Dimension location) {
        super(KoalaImg,playerController,location);
    }
    
}
