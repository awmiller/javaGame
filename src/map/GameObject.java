/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author awmil_000
 */
public class GameObject implements Observer{
    
    private BufferedImage Image;

    private Dimension Location;   
    public Dimension getLocation() {
        return Location;
    }
    
    private Dimension size;
    public Dimension getSize() {
        return size;
    }
    
    private boolean dispose;
    public boolean disposable(){return dispose;}
    
    public GameObject(BufferedImage image,Dimension location){
        Image = image;
        Location = location;
        dispose = false;
        size = new Dimension(Image.getHeight(),Image.getWidth());
    }
 

    @Override
    public void update(Observable o, Object arg) {
        GameEvent ge = (GameEvent)arg;
        switch(ge.getType()){
            case GameEvent.KEY_TYPE:
                
                break;
            case GameEvent.PLOT_TYPE:
                
                break;
        }
    }
    
    public boolean isColliding(GameObject other){
        //is distance between < 2R
        //subclasses should override for better behavior
        return false;
    }

    public boolean drawsBefore(GameObject obj) {
        return
        !((this.Location.height>obj.Location.height) || 
                ((this.Location.width > obj.Location.width)&&
                    (this.Location.height == obj.Location.height)));
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(Image,Location.width, Location.height,(ImageObserver)null);
    }
    
}
