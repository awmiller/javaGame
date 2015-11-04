/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

/**
 *
 * @author awmil_000
 */
public class Coordinates{

    public int x,y;
    public Coordinates(int X, int Y){this.x = X; this.y = Y;}

    Coordinates(int side_length) {
        x = side_length;
        y = x;
    }
}
