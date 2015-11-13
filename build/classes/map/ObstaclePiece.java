/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.Dimension;
import java.awt.image.BufferedImage;


public class ObstaclePiece extends GamePiece {

    public ObstaclePiece(BufferedImage image, Dimension location) {
        super(image, location);
        rigid = true;
    }   
}
