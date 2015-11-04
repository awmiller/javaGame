/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.event.KeyEvent;

/**
 *
 * @author awmil_000
 */
public class GameEvent {
    private int type;
    int getType(){
        return this.type;
    }

    public static final int KEY_TYPE =0;
    public static final int PLOT_TYPE =1;
    
    public class KeyType extends GameEvent{
        public KeyType(KeyEvent KE){
            ke = KE;
            type = KEY_TYPE;
        }
        public KeyEvent ke;
    }
    public class PlotType extends GameEvent{
        public PlotType(String e){
            event = e;
            type = PLOT_TYPE;
        }
        public String event;
    }
}
