/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import game.KeyController;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;


public class CharacterPiece extends GamePiece {
    
    protected int Health;
    public int getHealth() {
        return Health;
    }

    protected int Power;
    public int getPower() {
        return Power;
    }

    protected int Armor;
    public void setArmor(int level){
        Armor =level;
    }
    
    public int takesDamage(int power){
        int damage = (power-Armor)>0?(power-Armor):0;
        Health -= damage;
        return damage;
    }
    
    @Override
    public boolean drawsBefore(GamePiece obj) {
        return 
        (this.Location.height>obj.Location.height) || 
                ((this.Location.width < obj.Location.width)&&
                    (this.Location.height == obj.Location.height));
    }

    @Override
    public boolean isColliding(GamePiece other) {
        Rectangle me = Game.getRectCollider(this.Location,this.size);
        Rectangle you = Game.getRectCollider(other.Location, other.size);
        return me.intersects(you);
    } 

    public CharacterPiece(BufferedImage image,KeyController kc, Dimension location) {
        super(image, location);
        mControlls = kc;
        mControlls.attach(mcmi);
    }
 
    private KeyController mControlls;
    private KeyController.ControlModelInterface mcmi = new KeyController.ControlModelInterface() {

        @Override
        public void onEvent() {
            for (GameEvent evt : mControlls.eventQueue) {
                setChanged();
                notifyObservers(evt);
                }
            clearChanged();
        }
        
    };
}
