/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
import game.Game;
import game.KeyController;
import game.MoveEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author awmil_000
 */
public class TankPiece extends CharacterPiece{

    public TankPiece(BufferedImage image, KeyController kc, Dimension location) {
        super(image, kc, location);
    }
    
    
    private double RADIANS_PER_FRAME = Math.PI/(1*Game.FRAMES_PER_SECOND);
    private static int epsillon = 10;
    public static final BufferedImage weaponStrip = Game.getSprite("/res/Weapon_strip3.png");
    public static final BufferedImage shieldImg = Game.getSprite("/res/Shield1.png");
    
    //private int speed elevated to super
    int SpeedDivider = 4;
    int BoostDuration = 0;
    private Dimension ThisMove = Game.ZERO_VECTOR;
    public void boostSpeed(int duration){
        if(BoostDuration > 0) 
        {
            BoostDuration = duration;
            return;
        }
        SpeedDivider = 8;
        setSpeed(12);
        equippedWeapon.attackSpeed=20;
        BoostDuration = duration;
    }
    
    protected int AttackCooldown = 0;
    
    public AffineTransform rotation = new AffineTransform();
    
    private int WeaponEquipDuration = 0;
    private AttackEvent equippedWeapon = AttackEvent.BulletAttack;
    public void equipWeapon(AttackEvent weapon){
        equippedWeapon = weapon;
    }
    
    private double heading;
    public final double getHeading(){return heading;}

    
    @Override
    public void draw(Graphics2D g2d) {

        AffineTransformOp atxop = new AffineTransformOp(rotation,AffineTransformOp.TYPE_BILINEAR);

        g2d.drawImage(super.Image, atxop, Location.width-size.width/2, Location.height-size.height/2);
        
        float percent = (float)Health/(float)maxHealth;
        int red = Math.round(255 - (float)Math.pow(255, percent));
        int green = Math.round((float)Math.pow(255, percent));//
        g2d.setColor(new Color(red,green,0,255));
        g2d.fillRect(Location.width-size.width/2, Location.height+size.height/2,Health,5);
        
        if(equippedWeapon.Subtype==AttackEvent.BOUNCING_ATTACK){
            g2d.drawImage(
                    weaponStrip.getSubimage(weaponStrip.getWidth()/3,0, 
                            weaponStrip.getWidth()/3, weaponStrip.getHeight()),
                    Location.width-size.width/2, 
                    Location.height+size.height/2 - weaponStrip.getHeight(),
                    null);
        }else if(equippedWeapon.Subtype==AttackEvent.MISSILE_ATTACK){
            g2d.drawImage(
                    weaponStrip.getSubimage(0,0, 
                            weaponStrip.getWidth()/3, weaponStrip.getHeight()),
                    Location.width-size.width/2, 
                    Location.height+size.height/2 - weaponStrip.getHeight(),
                    null);
        }
        if(Armor > 0){
            g2d.drawImage(shieldImg, 
                    Location.width-shieldImg.getWidth()/2,
                    Location.height-shieldImg.getHeight()/2,null);
        }
    }
    
    @Override
    public void move() {
        super.move();
        NextMove = Game.ZERO_VECTOR;
        int move = 0;
        
        if(AttackCooldown >0){
            AttackCooldown--;
        }
        
        if(BoostDuration > 0){
            BoostDuration--;
            if(BoostDuration<=0){
                SpeedDivider = 4;
                BoostDuration=0;
                setSpeed(6);
                equippedWeapon.attackSpeed-=12;
            }
        }
        
        if(WeaponEquipDuration > 0){
            WeaponEquipDuration--;
            if(WeaponEquipDuration <=0){
                WeaponEquipDuration=0;
                Power = 5;
                equippedWeapon = AttackEvent.BulletAttack;
            }
        }

        if (mControlls.eventQueue.contains(MoveEvent.RotateRight)) {
            setChanged();
            heading += RADIANS_PER_FRAME;
            rotation.rotate(RADIANS_PER_FRAME,size.width/2,size.height/2);

        }
        if (mControlls.eventQueue.contains(MoveEvent.RotateLeft)) {
            setChanged();
            heading -= RADIANS_PER_FRAME;
            rotation.rotate(-RADIANS_PER_FRAME,size.width/2,size.height/2);
        }

        if (mControlls.eventQueue.contains(MoveEvent.MoveUp)) {
            setChanged();
            move -= getSpeed();
        }
        if (mControlls.eventQueue.contains(MoveEvent.MoveDown)) {
            setChanged();
            move += getSpeed();
        }
        
        NextMove = Game.rotate(move, heading);
        
        if(mControlls.eventQueue.contains(AttackEvent.PendingAttack)){
            
//            System.out.printf("\nATTACK COOLDOWN %d :: Frame %d", AttackCooldown,FrameCount);
//            mControlls.eventQueue.remove(AttackEvent.MissileAttack);
            if(AttackCooldown ==0){
                AttackCooldown = Game.FRAMES_PER_SECOND/SpeedDivider;
                setChanged();
                notifyObservers(equippedWeapon);
                clearChanged();
                return;
            }
        }

        notifyObservers();
        clearChanged();
    }
    
    @Override
    public void onCollide(GamePiece collider) {
        if(collider instanceof PowerUpPiece){
            switch(((PowerUpPiece)collider).getType()){
                case PowerUpPiece.POWER_BOUNCE:
                    this.Power +=5;
                    this.WeaponEquipDuration = Game.FRAMES_PER_SECOND*10;
                    this.equipWeapon(AttackEvent.BouncingAttack);
                    break;
                case PowerUpPiece.POWER_MISSILE:
                    this.Power +=10;
                    this.WeaponEquipDuration = Game.FRAMES_PER_SECOND*10;
                    this.equipWeapon(AttackEvent.MissileAttack);
                    break;
                case PowerUpPiece.POWER_SHIELD:
                    this.Armor=10;
                    break;
                case PowerUpPiece.POWER_TURRET:
                    Health+= 10; if(Health>maxHealth) Health = maxHealth;
                    WeaponEquipDuration=0;
                    Power = 5;
                    equippedWeapon = AttackEvent.BulletAttack;
                    break;
                case PowerUpPiece.POWER_FASTFORWARD:
                    this.boostSpeed(Game.FRAMES_PER_SECOND*10);
                    break;
                case PowerUpPiece.POWER_SUPERBOUNCE:
                    equippedWeapon = AttackEvent.SuperBouncingAttack;
                    Power = 30;
                    this.WeaponEquipDuration = Game.FRAMES_PER_SECOND*10;
                    break;
            }
            collider.onCollide(this);
        }
        
    }
    
    @Override
    public int collideRadius() {
        return radius() - epsillon;
    }
}


    