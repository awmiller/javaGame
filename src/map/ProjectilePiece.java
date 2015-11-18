/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 *
 * @author Andros
 */
class ProjectilePiece extends GamePiece {

    private double Heading;
    private int Power;
    
    ProjectilePiece(double heading, int power, Dimension location) {
        super(Game.getSprite("/res/Shell.gif"),location);
        Heading = heading;
        Power = power;
    }

    @Override
    public void move() {
        super.move(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawsBefore(GamePiece obj) {
        return super.drawsBefore(obj); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
