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
public class KoalaAnimator extends FrameAnimator{

    private BufferedImage Strip = Game.getCompatSprite("/res/kbr8/Koala_stand.png");
    private int progress = 0;
    
    public static int TransitionSpeed = 2;
    public KoalaAnimator(){
        super(TransitionSpeed);
        Frame = Game.getCompatImage(Frame, 40, 40);
        Frame = this.Strip.getSubimage(0, 0, 40, 40);
    }
    @Override
    public BufferedImage getFrame() {
        return Frame;
    }

    @Override
    public void onTransition() {
       
//            System.out.printf("Progress: %d\n",progress);
            if((progress< (Strip.getWidth()-40)&&(getState()!=0))){
                Frame = this.Strip.getSubimage(progress, 0, 40, 40);
                progress+=40;
            }else{
                progress=0;
                switch(getState()){
                case 1:
                    Strip = Game.getCompatSprite("/res/kbr8/Koala_down.png");
                    break;
                case 2:
                    Strip = Game.getCompatSprite("/res/kbr8/Koala_up.png");
                    break;
                case 3:
                    Strip = Game.getCompatSprite("/res/kbr8/Koala_left.png");
                    break;
                case 4:
                    Strip = Game.getCompatSprite("/res/kbr8/Koala_right.png");
                    break;
                case 0:
                    Strip = Game.getCompatSprite("/res/kbr8/Koala_stand.png");
            }
        }
    }
    
}
