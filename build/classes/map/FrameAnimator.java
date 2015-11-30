/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.image.BufferedImage;

/**
 * State machine for animating motion frame-by-frame
 * @author Andros
 */
abstract class FrameAnimator {
    private int state;
    private int FrameCount;
    private int TransitionFrameLimit;
    
    protected BufferedImage Frame;
    
    private boolean animating;
    public boolean toggleAnimation(){
        animating = !animating;
        return animating;
    }
    public boolean isAnimating(){
        return animating;
    }
    public boolean setAnimating(boolean b){
        animating = b;
        return animating;
    }
    
    public FrameAnimator(int FramePeriod){
        TransitionFrameLimit = FramePeriod;
        state =0;
    }
    
    public abstract BufferedImage getFrame();
    
    public abstract void onTransition();
    
    public int update(int code){
        FrameCount++;
        if(FrameCount > TransitionFrameLimit){
            FrameCount =0;
            onTransition();
            state = code;
//            System.out.printf("State: %d",state);
        }
        return state;
    }
    
    public final int getState(){return state;}
}
