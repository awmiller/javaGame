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
 * Creates a lock/switch combo by referencing a Map and adding a lock in a valid
 * location saved by Map.createWalls(). Switch placement is determined by the Map
 * and lock placement is handled here.
 * @author awmil_000
 */
class LockSwitchPieces extends GamePiece {

    private static BufferedImage YellowLock = Game.getCompatSprite("/res/kbr8/Lock_yellow.png");
    private static BufferedImage RedLock = Game.getCompatSprite("/res/kbr8/Lock_red.png");
    private static BufferedImage BlueLock = Game.getCompatSprite("/res/kbr8/Lock_blue.png");
    
    private static BufferedImage YellowSwitch = Game.getCompatSprite("/res/kbr8/Switch_yellow.png");
    private static BufferedImage RedSwitch = Game.getCompatSprite("/res/kbr8/Switch_red.png");
    private static BufferedImage BlueSwitch = Game.getCompatSprite("/res/kbr8/Switch_blue.png");
    
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
    KoalaMap mContainer;
    int SwitchState = STATE_LEFT;
    private static int STATE_LEFT = 1;
    private static int STATE_RIGHT =2;
    private BufferedImage strip;
    
    
    public LockSwitchPieces(Dimension dimension, char c, KoalaMap map) {
        super(getImage(c).getSubimage(0, 0, 40, 40),dimension);
        strip = getImage(c);
        switch(c){
            case 'Y':
            case 'B':
                type = c;
                break;
            default:
                type = 'R';
        }
        mContainer = map;
        nonRigidCollisions = true;
    }
    
    private LockPiece thisLock;
    private class LockPiece extends GamePiece{
        public LockPiece(BufferedImage image, ArrayList<Dimension> locations) {
            super(image, locations.remove(0));
            rigid = true;
        }
        
        //timer variable to regerate lock
        public int reset =0;
        public int resetValue = 300;
        
        /**
         * flips the parent Switch and plays sound, hiding this lock piece
         */
        public void flip(){flip(resetValue);}
        public void flip(int reload){
            if(reset==0){
                reset = reload;
                Game.playClip("/res/kbr8/Click.wav");
                switchImage();
            }
        }

        /**
         * use the lock's dispose event to play switch sound and set timer
         */
        @Override
        public void onDispose() {
            flip();
        }

        /**
         * use the move event to count down the lock respawn timer
         */
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

    protected void switchImage(){
        if(SwitchState == STATE_LEFT){
                    super.Image = strip.getSubimage(40, 0, 40, 40);
                    SwitchState = STATE_RIGHT;
                }else{
                    super.Image = strip.getSubimage(0, 0, 40, 40);
                    SwitchState = STATE_LEFT;
                }
    }
    
    /**
     * Uses the onStartGame() event to initialize the lock piece
     */
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

    //track a colliding stone for forcing the switch down.
    private StonePiece stoneCollisionTracker;
    @Override
    public void onCollide(GamePiece collider) {
        thisLock.dispose = true;
        //use a rediculously high reset value to implement stone behavior
        if(collider instanceof StonePiece){
            thisLock.flip(100000);
            stoneCollisionTracker = (StonePiece) collider;
        }
    }

    @Override
    public void move() {
        thisLock.move();
        
        //If no longer colliding with a stone, reset the lock countdown to a normal time
        if((stoneCollisionTracker!=null)&&
                !this.isColliding(stoneCollisionTracker)){
            stoneCollisionTracker = null;
            thisLock.reset = 300;
        }
    
    }

    /**
     * always draw switches on bottom.
     * @param obj
     * @return 
     */
    @Override
    public boolean drawsBefore(GamePiece obj) {
        return true;
    }
    
    
    
    
    
    
    
}
