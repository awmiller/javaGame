/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import map.*;

/**
 *
 * @author awmil_000
 */
public class KeyController extends KeyAdapter{
    public ArrayList<GameEvent> eventQueue;
        public KeyController(char[] map){
            eventQueue = new ArrayList<>();
            UP_CHAR = map[0];
            LEFT_CHAR = map[1];
            DOWN_CHAR = map[2];
            RIGHT_CHAR = map[3];
            FIRE_CHAR = map[4];
        }
        
        private ControlModelInterface mModel;

        @Override
        public void keyPressed(KeyEvent e)
        {
            char code =e.getKeyChar();
            if(code==UP_CHAR){
                    eventQueue.add(new MoveEvent(MoveEvent.UP_MOVE));
            } else if(code==DOWN_CHAR){
                    eventQueue.add(new MoveEvent(MoveEvent.DOWN_MOVE));
            } else if(code==LEFT_CHAR){
                    eventQueue.add(new MoveEvent(MoveEvent.LEFT_ROTATE));
            } else if(code==RIGHT_CHAR){
                    eventQueue.add(new MoveEvent(MoveEvent.RIGHT_ROTATE));
            } else if(code==FIRE_CHAR){
                    eventQueue.add(new AttackEvent());
            }
            mModel.onEvent();
            eventQueue.clear();
        }


        @Override
        public void keyReleased(KeyEvent e)
        {
            System.out.println("KeyReleased!"+ e.getKeyChar());
        }

    public void attach(ControlModelInterface cmi) {
       
            mModel = cmi;
    }
        
        public interface ControlModelInterface{
            void onEvent();
        }
        
        private final char UP_CHAR;
        private final char DOWN_CHAR;
        private final char LEFT_CHAR;
        private final char RIGHT_CHAR;
        private final char FIRE_CHAR;
    }
