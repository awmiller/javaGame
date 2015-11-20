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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Observable;


public class CharacterPiece extends GamePiece {
    
    private double RADIANS_PER_FRAME = Math.PI/(1*Game.FRAMES_PER_SECOND);
    private static int epsillon = 20;
    public static final BufferedImage weaponStrip = Game.getSprite("/res/Weapon_strip3.png");
    public static final BufferedImage shieldImg = Game.getSprite("/res/Shield1.png");
    
    
    private int speed;
    int SpeedDivider = 4;
    int BoostDuration = 0;
    public void boostSpeed(int duration){
        if(BoostDuration > 0) 
        {
            BoostDuration = duration;
            return;
        }
        SpeedDivider = 8;
        speed = 12;
        equippedWeapon.attackSpeed=20;
        BoostDuration = duration;
    }
    
    private int maxHealth = 60;
    
    protected int Health;
    public int getHealth() {
        return Health;
    }

    protected int Power = 5;
    public int getPower() {
        return Power;
    }
    
    private int WeaponEquipDuration = 0;
    private AttackEvent equippedWeapon = AttackEvent.BulletAttack;
    public void equipWeapon(AttackEvent weapon){
        equippedWeapon = weapon;
    }

    protected int Armor;
    public void setArmor(int level){
        Armor =level;
    }
    
    private int Score=0;
    @Override
    public int addScore(int i) {
        Score += i;
        return Score;
    }
    public final int getScore(){return Score;}
    
    private double heading;
    public final double getHeading(){return heading;}
    
    public int takesDamage(int power){
        int damage = (power-Armor)>0?(power-Armor):0;
        Health -= damage;
        if(Health <=0) {
            dispose =true;
            this.Respawn = 2*Game.FRAMES_PER_SECOND;
        }
        if(Armor>0){Armor-=damage;}
        return damage;
    }
    
    @Override
    public boolean drawsBefore(GamePiece obj) {
        return 
        (this.Location.height>obj.Location.height) || 
                ((this.Location.width < obj.Location.width)&&
                    (this.Location.height == obj.Location.height));
    }     

    public CharacterPiece(BufferedImage image,KeyController kc, Dimension location) {
        super(image, location);
        mControlls = kc;
        mControlls.attach(mcmi);
        speed = 6;
        this.rigid =true;
        Health = maxHealth;
    }

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
    
    
 
    
    public AffineTransform rotation = new AffineTransform();
    private KeyController mControlls;
    private KeyController.ControlModelInterface mcmi = new KeyController.ControlModelInterface() {
        

        @Override
        public void onEvent() {
            
        }
    };
    
    int FrameCount = 0;
    int AttackCooldown = 0;

    @Override
    public void move() {
        FrameCount++;
        hasMoved = true;
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
                speed = 6;
                equippedWeapon.attackSpeed=0;
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
            move -= speed;
        }
        if (mControlls.eventQueue.contains(MoveEvent.MoveDown)) {
            setChanged();
            move += speed;
        }
        
        NextMove = Game.rotate(move, heading);
        
        if(mControlls.eventQueue.contains(AttackEvent.PendingAttack)){
            if(AttackCooldown ==0){
                AttackCooldown = Game.FRAMES_PER_SECOND/SpeedDivider;
                setChanged();
                notifyObservers(equippedWeapon);
                clearChanged();
//                if(equippedWeapon == AttackEvent.SuperBouncingAttack){
//                    WeaponEquipDuration =2;
//                }
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
    public void onRespawn(){
        super.onRespawn();
        Health = maxHealth;
    }
    
    @Override
    public int collideRadius() {
        return radius() - epsillon;
    }    
    
    
//
//    @Override
//    public boolean isColliding(GamePiece other) {
//        return
//        (getCollider().intersects(other.getCollider().getBounds())) &&
//        (other.getCollider().intersects(getCollider().getBounds()));
//    }
//    

//    @Override
//    public Shape getCollider() {
//        Dimension topl = new Dimension(
//                Location.width - size.width/2 + epsillon,
//                Location.height - size.height/2 + epsillon);
//        Dimension sz = new Dimension( 
//                size.width - 2*epsillon,
//                size.height - 2*epsillon);
//        AffineTransform af = new AffineTransform();
//        af.rotate(heading, Location.width, Location.height);
//        return af.createTransformedShape(new Rectangle(topl.width,topl.height,sz.width,sz.height));
//    }

    @Override
    boolean isColliding(Rectangle R1) {
        Shape s = new Ellipse2D.Double(Location.width-size.width/2+epsillon/2,
                Location.height-size.height/2+epsillon/2,
                size.width-epsillon,size.height-epsillon);
        return s.intersects(R1);
    }
    
    

}
