/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.event.KeyAdapter;

/**
 *
 * @author awmil_000
 */
public interface Controller {  
    public void onMove(MoveEvent direction);
    public void onAttack(AttackEvent Ae);
}
