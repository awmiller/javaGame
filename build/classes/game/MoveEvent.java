/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import map.GameEvent;
import static map.GameEvent.ATTACK_EVENT;
import static map.GameEvent.MOVE_THIS_PIECE;

/**
 *
 * @author awmil_000
 */
    public class MoveEvent extends GameEvent{
    static Dimension LEFT_ROTATE = new Dimension(-1,0);
    static Dimension RIGHT_ROTATE = new Dimension(1,0);
    static Dimension UP_MOVE = new Dimension(0,-1);
    static Dimension DOWN_MOVE = new Dimension(0,1);
        public Dimension vector;
        public AffineTransform transform;
        public MoveEvent(Dimension move){
            super();
            type = GameEvent.MOVE_THIS_PIECE;
            vector = move;
        }
        public MoveEvent(String transform_code){
            super();
            type = GameEvent.MOVE_THIS_PIECE;
            vector = Game.ZERO_VECTOR;
        }
        
        public final static MoveEvent MoveUp = new MoveEvent(MoveEvent.UP_MOVE);
        public final static MoveEvent MoveDown = new MoveEvent(MoveEvent.DOWN_MOVE); 
        public final static MoveEvent RotateLeft = new MoveEvent(MoveEvent.LEFT_ROTATE);
        public final static MoveEvent RotateRight = new MoveEvent(MoveEvent.RIGHT_ROTATE);
    }
