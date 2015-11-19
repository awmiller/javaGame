/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import map.Map;

/**
 *
 * @author Andros
 */
class MiniMap {
    
    BufferedImage img;
    Map mMap;
    int scale,width,height;

    public MiniMap(Map gameMap, int i) {
        scale = i;
        mMap = gameMap;
        img = Game.getCompatImage(img,300,300);
        width = mMap.getCorner().width/scale;
        height = mMap.getCorner().height/scale;
    }
    
    public void draw(Graphics2D g2d, Dimension viewCenter){  
        int x = viewCenter.width - width/2;
        int y = viewCenter.height - height;
        
        Graphics2D gmm = img.createGraphics();
        gmm.drawImage(mMap.printObjectsImage().getScaledInstance(width, height, Image.SCALE_FAST),
                0,0,null);
        gmm.dispose();
        g2d.drawImage(img, x, y, null);
    }
    
    
    
}
