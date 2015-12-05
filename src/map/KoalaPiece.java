/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import game.KeyController;
import game.KeyController;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import map.CharacterPiece;
import map.FrameAnimator;
import map.GamePiece;
import map.KoalaAnimator;

/**
 *
 * @author awmil_000
 */
public class KoalaPiece extends CharacterPiece {

    public KoalaPiece(BufferedImage KoalaImg, KeyController playerController, Dimension location) {
        super(KoalaImg,playerController,location);
        mFrameAnimator.setAnimating(true);
    }
    
    private FrameAnimator mFrameAnimator = new KoalaAnimator();
    public void setAnimator(FrameAnimator anm){
        mFrameAnimator = anm;
    }
    private int AnmState =0;
    
    private static final int MOVETIMER_RELOAD =40;
    private int MoveTimer = 0;
    private void setMoveTimer(){
        if(super.mControlls.eventQueue.size()>0){
            MoveTimer = MOVETIMER_RELOAD;
            Character c = super.mControlls.eventQueue.peek();
            if (c.equals(mControlls.UP_CHAR)) {
                NextMove = new Dimension(0,-1);
                AnmState = 2;
            }
            else if (c.equals(mControlls.DOWN_CHAR)) {
                NextMove = new Dimension(0,1);
                AnmState =1;
            }
            else if (c.equals(mControlls.LEFT_CHAR)) {
                NextMove = new Dimension(-1,0);
                AnmState = 3;
            }
            else if (c.equals(mControlls.RIGHT_CHAR)) {
                NextMove = new Dimension(1,0);
                AnmState=4;
            }
        }else{
            AnmState=0;
            
            while((topLeft().width%40>0)||(topLeft().height%40>0)){
                
                Dimension oldLocation = Location;
                
                if(topLeft().width%40 > 20){
                    NextMove = new Dimension(1,0);
                }else if((topLeft().width%40) !=0){
                    NextMove = new Dimension(-1,0);
                }else if(topLeft().height%40 > 20){
                    NextMove = new Dimension(0,1);
                }else{
                    NextMove = new Dimension(0,-1);
                }
                System.out.printf("Location %d,%d\n", topLeft().width,topLeft().height);
                setChanged();
                notifyObservers();
                clearChanged();
                
                if(oldLocation.equals(Location)){
                    break;
                }
            }
            NextMove = Game.ZERO_VECTOR;
        }
    }
    
    
    @Override
    public void move() {
        super.move();
        
        if (MoveTimer > 1) {
            MoveTimer--;
        } else {
            setMoveTimer();
        }
        
        setChanged();
        notifyObservers();
        clearChanged();
    }
    
        @Override
    public void draw(Graphics2D g2d) {
        mFrameAnimator.update(AnmState);
        
        g2d.drawImage(mFrameAnimator.getFrame(), Location.width-size.width/2, Location.height-size.height/2,null);
        
    }
}
