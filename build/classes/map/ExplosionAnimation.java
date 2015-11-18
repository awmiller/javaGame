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
class ExplosionAnimation extends GamePiece {

    public static int SMALL = 1;
    public static int LARGE = 2;
    
    static Dimension small = new Dimension(192,32);
    static Dimension large = new Dimension(448,64);
    
    static BufferedImage smallImage = Game.getSprite("/res/Explosion_small.png");
    static BufferedImage largeImage = Game.getSprite("/res/Explosion_large.png");
    
    static{
        small = new Dimension(smallImage.getWidth(),smallImage.getHeight());
        large = new Dimension(largeImage.getWidth(),largeImage.getHeight());
    }
    private BufferedImage strip;
    public ExplosionAnimation(BufferedImage img, Dimension location,BufferedImage gif) {
        super(img,location);
        strip = gif;
        stripinc = img.getWidth();
    }
    
    public static ExplosionAnimation getExplosion(Dimension location, int type){
        BufferedImage exp;
        ExplosionAnimation explosion;
         if(type==SMALL){
            exp = smallImage.getSubimage(0, 0, 32, 32);
            explosion = new ExplosionAnimation(exp,location,smallImage);
        } else{
            exp = largeImage.getSubimage(0, 0, 64, 64);
            explosion = new ExplosionAnimation(exp,location,largeImage);
        }
        
        return explosion;
    }

    int FrameCount=0;
    int stripx =0;
    int stripinc;
    
    @Override
    public void move() {
        
        FrameCount++;
        if((FrameCount % (Game.FRAMES_PER_SECOND/4))==0){
            stripx += stripinc;
            this.Image = strip.getSubimage(stripx, 0, stripinc,strip.getHeight());
        }
        
    }

    @Override
    public boolean drawsBefore(GamePiece obj) {
        return super.drawsBefore(obj); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
