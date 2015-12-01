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
 * Implements a Saw obstacle that can move and kill Koalas.
 * @author awmil_000
 */
class SawPiece extends GamePiece {

    /**
     * Movement mode horizontal
     */
    public static final int HORIZONTAL = 1;
    /**
     * Movement mode vertical
     */
    public static final int VERTICAL = 2;
    
    /**
     * Pixels per frame movement speed
     */
    int speed = 1;
    /**
     * Movement mode storage
     */
    int MovementMode = 0;
    
    /**
     * Store the animation state
     */
    int ImageState = STATE_LEFT;
    private static int STATE_LEFT = 1;
    private static int STATE_RIGHT =2;
    /**
     * A strip of frames for animation
     */
    private BufferedImage strip;
    
    private static final BufferedImage VerticalSaw = Game.getCompatSprite("/res/kbr8/Saw_vertical.png");
    private static final BufferedImage HorizSaw = Game.getCompatSprite("/res/kbr8/Saw_horizontal.png");
    
    /**
     * Create a saw piece @ dimension with the specified movement mode.
     * @param dimension starting location
     * @param moveMode movement mode
     * @see Map.SawPiece.HORIZONTAL
     * @see Map.SawPiece.VERTICAL
     */
    public SawPiece(Dimension dimension, int moveMode) {
        super(getImage(moveMode).getSubimage(0, 0, 40, 40),dimension);
        strip = getImage(moveMode);
        MovementMode =moveMode;
        if(MovementMode == HORIZONTAL){
            NextMove = new Dimension(speed,0);
        }else if(MovementMode == VERTICAL){
            NextMove = new Dimension(0,speed);
        }
        rigid = true;
    }
    
    /**
     * chooses the correct image for the super constructor
     * @param i
     * @return 
     */
    private static BufferedImage getImage(int i){
        switch(i){
            case HORIZONTAL:
                return HorizSaw;
            default:
                return VerticalSaw;
        }
        
        
    }

    /**
     * Allow the saw piece to collide with object and reverse direction.
     * @param collider 
     */
    @Override
    public void onCollide(GamePiece collider) {
        //cuts through characters
        if(collider instanceof CharacterPiece){
            collider.dispose = true;
            Game.playClip("/res/kbr8/Saw.wav");
        }
        else{
            speed = -speed;
            if(MovementMode == HORIZONTAL){
                NextMove = new Dimension(speed,0);
            }else if(MovementMode == VERTICAL){
                NextMove = new Dimension(0,speed);
            }
            
            //mitigate clipping error where saw goes through walls
            // TODO: make this better
            Dimension oldLoc = Location;
            this.Location = Game.add(this.Location, NextMove);
            if(isColliding(collider)){
                this.Location = oldLoc;
            }
        }       
    }

    //slow down rendering speed to improve animation quality
    int FrameDivider = 0;
    @Override
    public void move() {
        if(FrameDivider++ == 10){
            switchImage();
            FrameDivider = 0;
        }
        setChanged();
        notifyObservers();
        clearChanged();
    }
    
    protected void switchImage(){
        if(ImageState == STATE_LEFT){
                    super.Image = strip.getSubimage(40, 0, 40, 40);
                    ImageState = STATE_RIGHT;
                }else{
                    super.Image = strip.getSubimage(0, 0, 40, 40);
                    ImageState = STATE_LEFT;
                }
    }
    
    
    
}
