/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import map.GameEvent;
import static map.GameEvent.ATTACK_EVENT;

/**
 *
 * @author awmil_000
 */
public final class AttackEvent extends GameEvent {

    public Dimension target;
    public Dimension origin;
    public String Subtype;
    public int attackSpeed = 0;
    public static final String MISSILE_ATTACK = "Attack Event! Type: Missle";
    public static final String BULLET_ATTACK = "Attack Event! Type: Bullet";
    public static final String BOUNCING_ATTACK= "Attack Event! Type: Bouncer";

    public AttackEvent() {
        super();
    }

    public AttackEvent(String subtype) {
        super();
        type = GameEvent.ATTACK_EVENT;
        target = null;
        Subtype = subtype;
    }

    public static final AttackEvent MissileAttack = new AttackEvent(MISSILE_ATTACK);
    public static final AttackEvent BulletAttack = new AttackEvent(BULLET_ATTACK);
    public static final AttackEvent BouncingAttack = new AttackEvent(BOUNCING_ATTACK);
    public static final AttackEvent PendingAttack = new AttackEvent();
}
