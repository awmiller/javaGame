/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import map.GameEvent;
import static map.GameEvent.ATTACK_EVENT;

/**
 *
 * @author awmil_000
 */
    public class AttackEvent extends GameEvent{
        public Dimension target;
        public AttackEvent(Dimension clicked){
            super();
            type = GameEvent.ATTACK_EVENT;
            target = clicked;
        }

    public AttackEvent() {
        
            super();
            type = GameEvent.ATTACK_EVENT;
            target = null;
    }
}
