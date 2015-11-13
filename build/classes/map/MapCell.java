/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.util.ArrayList;

/**
 *
 * @author awmil_000
 */
class MapCell {
    ArrayList<GamePiece> contents;
    MapTile background;
    public MapCell(int TileId){
        background = new MapTile(TileId);
        contents = new ArrayList();
    }
}
