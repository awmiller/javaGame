/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author awmil_000
 */
public class Tile {

    public BufferedImage img;
    public String src;
    public Tile(String path){
        URL url = getClass().getResource(path);
        try {
            img = ImageIO.read(url);
            System.out.print(url);
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String toString() {
        return src;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == Tile.class){
            if(((Tile)obj).src == this.src){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = 0;
        for(char c : src.toCharArray()){
            code+= c;
            code = code*256;
        }
        return code;
    }
    
    
}
