/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
import game.Game;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import map.CharacterPiece;
import map.Map;
import map.ProjectilePiece;

/**
 *
 * @author awmil_000
 */
public class TankMap extends Map {

    public TankMap(String tank1map) throws Exception {
        super(tank1map);
        /**
         * Create the backdrop tiles
         */
        ArrayList<Tile> tiles;
        tiles = new ArrayList();
        /**
         * add tiles to static image
         */
        int area = corner.height * corner.width;
        for (int i = 0; i < area; i++) {
            tiles.add(Tiles.getRandom());
        }
   
    }

    @Override
    public BufferedImage createBackground() {
        return Game.getCompatImage(new BufferedImage(1,2,3), 600, 600);
    }
    
    @Override
    public void update(Observable o, Object arg) { 
        
        if(o instanceof TankPiece){
        
            TankPiece tp = (TankPiece) o;

            if(arg instanceof AttackEvent){
//                ProjectilePiece pjtl = new ProjectilePiece(cp.getHeading(),cp.getPower(),cp.Location);
                add(ProjectilePiece.getProjectile((AttackEvent) arg,tp.Location,tp.getHeading()));
//                System.out.print("\nProjectile Created!");
            }
        }
    }

    int FrameCount=0;
    @Override
    public void moveAll() {
        FrameCount++;        
         if ((FrameCount % (Game.FRAMES_PER_SECOND * 8) == 0)
                && (pickupSpawns.size() > 0)) {
            Dimension random = pickupSpawns.get(
                    Math.round((float) Math.random() * (pickupSpawns.size() - 1)));
            pickupSpawns.remove(random);
            add(PowerUpPiece.getRandom(random));
        }
         
        super.moveAll(); 
    }

    @Override
    public void createWalls() {
        
    }
    
    
    
}
