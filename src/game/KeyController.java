/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author awmil_000
 */
public class KeyController extends KeyAdapter{
        public KeyController(){
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            System.out.println("KeyPressed!"+ e.getKeyChar());
        }


        @Override
        public void keyReleased(KeyEvent e)
        {
            System.out.println("KeyReleased!"+ e.getKeyChar());
        }
    }
