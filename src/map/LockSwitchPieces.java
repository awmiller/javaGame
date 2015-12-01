/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author awmil_000
 */
class LockSwitchPieces extends GamePiece {

    private static BufferedImage YellowLock = Game.getCompatSprite("/res/kbr8/Lock_yellow.gif");
    private static BufferedImage RedLock = Game.getCompatSprite("/res/kbr8/Lock_red.gif");
    private static BufferedImage BlueLock = Game.getCompatSprite("/res/kbr8/Lock_blue.gif");
    
    private static BufferedImage YellowSwitch = Game.getCompatSprite("/res/kbr8/Switch_yellow.gif");
    private static BufferedImage RedSwitch = Game.getCompatSprite("/res/kbr8/Switch_red.gif");
    private static BufferedImage BlueSwitch = Game.getCompatSprite("/res/kbr8/Switch_blue.gif");
    
    private static BufferedImage getImage(char c){
        switch(c){
            case 'Y':
                return YellowSwitch;
            case 'B':
                return BlueSwitch;
            default:
                return RedSwitch;
        }
    }
    
    char type;
    Map mContainer;
    
    public LockSwitchPieces(Dimension dimension, char c, Map map) {
        super(getImage(c),dimension);
        switch(c){
            case 'Y':
            case 'B':
                type = c;
                break;
            default:
                type = 'R';
        }
        mContainer = map;
    }
    
    private LockPiece thisLock;
    private class LockPiece extends GamePiece{
        public LockPiece(BufferedImage image, ArrayList<Dimension> locations) {
            super(image, locations.remove(0));
            rigid = true;
        }
        public int reset =0;
        public int resetValue = 300;
        public void flip(){flip(resetValue);}
        public void flip(int reload){
            if(reset==0){
                reset = reload;
                Game.playClip("/res/kbr8/Click.wav");
            }
        }

        @Override
        public void onDispose() {
            flip();
        }

        @Override
        public void move() {
            if (reset > 0) {
                reset--;
                if (reset == 0) {
                    if (!mContainer.contents.contains(this)) {
                        mContainer.add(this);
                        this.dispose = false;
                        Game.playClip("/res/kbr8/Lock.wav");
                    }
                }
            }
        }

        
    }

    @Override
    public void onStartGame() {
        switch(type){
            case 'Y':
                if (mContainer.YellowLocks.size() > 0) {
                    thisLock = new LockPiece(YellowLock, mContainer.YellowLocks);
                    mContainer.add(thisLock);
                }
                break;
            case 'B':
                if (mContainer.BlueLocks.size() > 0) {
                    thisLock = new LockPiece(BlueLock, mContainer.BlueLocks);
                    mContainer.add(thisLock);
                }
                break;
            default:
                if (mContainer.RedLocks.size() > 0) {
                    thisLock = new LockPiece(RedLock, mContainer.RedLocks);
                    mContainer.add(thisLock);
                }
        }
    }

    @Override
    public void onCollide(GamePiece collider) {
        thisLock.dispose = true;
        if(collider instanceof StonePiece){
            thisLock.flip(100000);
        }
    }

    @Override
    public void move() {
        thisLock.move();
    }

    @Override
    public boolean drawsBefore(GamePiece obj) {
        return true;
    }
    
    
    
    
    
    
    
}
