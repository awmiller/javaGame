/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import map.*;

/**
 *
 * @author awmil_000
 */
public class KeyController extends KeyAdapter{
    public volatile ArrayDeque<Character> eventQueue;
        public KeyController(char[] map){
            eventQueue = new ArrayDeque<>();
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
            int sz = eventQueue.size();
            
            char code =e.getKeyChar();
            if(code==UP_CHAR){
                if(!eventQueue.contains(UP_CHAR))
                    eventQueue.push(UP_CHAR);
            } else if(code==DOWN_CHAR){
                if(!eventQueue.contains(DOWN_CHAR))
                    eventQueue.push(DOWN_CHAR);
            } else if(code==LEFT_CHAR){
                if(!eventQueue.contains(LEFT_CHAR))
                    eventQueue.push(LEFT_CHAR);
            } else if(code==RIGHT_CHAR){
                if(!eventQueue.contains(RIGHT_CHAR))
                    eventQueue.push(RIGHT_CHAR);
            } else if(code==FIRE_CHAR){
                if(!eventQueue.contains(FIRE_CHAR))
                    eventQueue.push(FIRE_CHAR);
            }
            if(sz != eventQueue.size()){
                mModel.onEvent();
            }
            //System.out.println("KeyPressed!"+ e.getKeyChar());
        }


        @Override
        public void keyReleased(KeyEvent e)
        {
            char code =e.getKeyChar();
            if(code==UP_CHAR){
                if(eventQueue.contains(UP_CHAR))
                    eventQueue.remove(UP_CHAR);
            } else if(code==DOWN_CHAR){
                if(eventQueue.contains(DOWN_CHAR))
                    eventQueue.remove(DOWN_CHAR);
            } else if(code==LEFT_CHAR){
                if(eventQueue.contains(LEFT_CHAR))
                    eventQueue.remove(LEFT_CHAR);
            } else if(code==RIGHT_CHAR){
                if(eventQueue.contains(RIGHT_CHAR))
                    eventQueue.remove(RIGHT_CHAR);
            } else if(code==FIRE_CHAR){
                if(eventQueue.contains(FIRE_CHAR))
                    eventQueue.remove(FIRE_CHAR);
            }else{
                mModel.onEvent();  
            }
//            System.out.println("KeyReleased!"+ e.getKeyChar());
        }

    public void attach(ControlModelInterface cmi) {
            mModel = cmi;
    }
        
        public interface ControlModelInterface{
            void onEvent();
        }
        
        public final char UP_CHAR;
        public final char DOWN_CHAR;
        public final char LEFT_CHAR;
        public final char RIGHT_CHAR;
        public final char FIRE_CHAR;
        
        
    }
