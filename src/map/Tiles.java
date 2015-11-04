/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.nio.file.Paths;
import java.util.ArrayList;
import map.Tile;

/**
 *
 * @author awmil_000
 */
public final class Tiles {
    public static Tile waterTile;
    public static Tile lavaTile;
    
    private static ArrayList<Tile> all;
    public static int STANDARD_WIDTH;
    public static int STANDARD_HEIGHT;
    
    static{
        System.out.print(Paths.get("").toAbsolutePath().toString());
        waterTile = new Tile("res/water.png");
        lavaTile = new Tile("res/lava.png");
        all = new ArrayList();
        all.add(waterTile);
        all.add(lavaTile);
        STANDARD_WIDTH = waterTile.img.getWidth();
        STANDARD_HEIGHT = waterTile.img.getHeight();
    }
    
    public static Tile getRandom() {
        int rando = (int)Math.round(Math.random() *(all.size()-1));
        return all.get(rando);
    }
}
