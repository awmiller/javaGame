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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


public class CharacterPiece extends GamePiece {
    
    
    public Dimension CurrentMove= Game.ZERO_VECTOR;
    private double RADIANS_PER_FRAME = Math.PI/(1*Game.FRAMES_PER_SECOND);
    private static int epsillon = 20;
    public static final BufferedImage weaponStrip = Game.getSprite("/res/Weapon_strip3.png");
    public static final BufferedImage shieldImg = Game.getSprite("/res/Shield1.png");
    
    private FrameAnimator mFrameAnimator = new KoalaAnimator();
    public void setAnimator(FrameAnimator anm){
        mFrameAnimator = anm;
    }
    private int AnmState =0;
    
    private static final int MOVETIMER_RELOAD =40;
    private int MoveTimer = 0;
    private void setMoveTimer(){
        if(mControlls.eventQueue.size()>0){
            MoveTimer = MOVETIMER_RELOAD;
            Character c = mControlls.eventQueue.peek();
            if (c.equals(mControlls.UP_CHAR)) {
                NextMove = new Dimension(0,-1);
                AnmState = 2;
            }
            else if (c.equals(mControlls.DOWN_CHAR)) {
                NextMove = new Dimension(0,1);
                AnmState =1;
            }
            else if (c.equals(mControlls.LEFT_CHAR)) {
                NextMove = new Dimension(-1,0);
                AnmState = 3;
            }
            else if (c.equals(mControlls.RIGHT_CHAR)) {
                NextMove = new Dimension(1,0);
                AnmState=4;
            }
        }else{
            AnmState=0;
            
            while((topLeft().width%40>0)||(topLeft().height%40>0)){
                
                Dimension oldLocation = Location;
                
                if(topLeft().width%40 > 20){
                    NextMove = new Dimension(1,0);
                }else if((topLeft().width%40) !=0){
                    NextMove = new Dimension(-1,0);
                }else if(topLeft().height%40 > 20){
                    NextMove = new Dimension(0,1);
                }else{
                    NextMove = new Dimension(0,-1);
                }
                System.out.printf("Location %d,%d\n", topLeft().width,topLeft().height);
                setChanged();
                notifyObservers();
                clearChanged();
                
                if(oldLocation.equals(Location)){
                    break;
                }
            }
            NextMove = Game.ZERO_VECTOR;
        }
    }
    
    private Dimension topLeft(){
        return new Dimension(Location.width-size.width/2,Location.height-size.height/2);
    }
    
    private int speed;
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
        speed = 12;
        equippedWeapon.attackSpeed=20;
        BoostDuration = duration;
    }
    public int setSpeed(int newSpeed){
        speed = newSpeed;
        return speed;
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
        
        mFrameAnimator.setAnimating(true);
        CurrentMove = Location;
    }

    @Override
    public void draw(Graphics2D g2d) {
        mFrameAnimator.update(AnmState);
        
        g2d.drawImage(mFrameAnimator.getFrame(), Location.width-size.width/2, Location.height-size.height/2,null);
        
        
//        float percent = (float)Health/(float)maxHealth;
//        int red = Math.round(255 - (float)Math.pow(255, percent));
//        int green = Math.round((float)Math.pow(255, percent));//
        g2d.setColor(Color.RED);
//        g2d.fillRect(Location.width-size.width/2, Location.height+size.height/2,Health,5);
        g2d.drawRect(Location.width-size.width/2, Location.height-size.height/2, size.width, size.height);
    }
    
    
 
    
    public AffineTransform rotation = new AffineTransform();
    private KeyController mControlls;
    private KeyController.ControlModelInterface mcmi = new KeyController.ControlModelInterface() {
        @Override
        public void onEvent() {          
        }
    };
    
    protected int FrameCount = 0;
    protected int AttackCooldown = 0;

    @Override
    public void move() {
        FrameCount++;
        hasMoved = true;
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
        
        if(MoveTimer >1){
            MoveTimer--;
        }else{
            setMoveTimer();
        }
        
        setChanged();
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
        CurrentMove = Location;
    }
    
    @Override
    public int collideRadius() {
        return radius() - epsillon;
    }

    @Override
    boolean isColliding(Rectangle R1) {
        Shape s = new Rectangle(Location.width-size.width/2,Location.height-size.height/2,
                size.width,size.height);
//        Ellipse2D.Double(Location.width-size.width/2+epsillon/2,
//                Location.height-size.height/2+epsillon/2,
//                size.width-epsillon,size.height-epsillon);
        return s.intersects(R1);
    }
    
    

}
