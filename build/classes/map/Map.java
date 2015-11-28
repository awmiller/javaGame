/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
import game.Game;
import game.MoveEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author awmil_000
 */
public class Map implements Observer{
    private boolean hasMoved = true;
    
    ArrayList<GamePiece> contents;
    ArrayList<String> ListLines = new ArrayList<>();
    ArrayList<Dimension> pickupSpawns = new ArrayList<>();
    ArrayList<Dimension> playerSpawns = new ArrayList<>();
    HashMap<Dimension,Integer> wallSpawns = new HashMap<>();
    
    static BufferedImage wall1 = (BufferedImage) Game.getSprite("/res/Wall1.gif");
    static BufferedImage wall2 = (BufferedImage) Game.getSprite("/res/Wall2.gif");
    private static final Dimension wallDimension = new Dimension(40,40);
    static{
        int wd = wallDimension.width;
        int ht = wallDimension.height;
        BufferedImage wallStrip = Game.getCompatSprite("/res/kbr8/Wall_tiles.png");
        wall1 = Game.getCompatImage(wall1, wd,ht);
        wall2 = Game.getCompatImage(wall2, wd,ht);
        Graphics2D g1 = wall1.createGraphics();
        g1.drawImage(wallStrip.getSubimage(0, 0, wd, ht),
                0,0, null);
        g1.dispose();
        
        Graphics2D g2 = wall2.createGraphics();
        g2.drawImage(wallStrip.getSubimage(wd, ht, wd, ht),
                0,0, null);
        g2.dispose();
    }
    
    
    public GamePiece add(GamePiece go){
        contents.add(go);
        go.addObserver(this);
        return go;
    }
    
        
    public GamePiece remove(GamePiece go){
        contents.remove(go);
        go.deleteObserver(this);
        return go;
    }
    /**
     * get a constant reference to contents
     * @return final contents
     */
    public final ArrayList<GamePiece> getObjects(){
        return contents;
    }
    
    /**
     * bottom right corner of the map
     */
    Dimension corner;
    
    public Dimension getCorner() {
        return new Dimension(corner.width,corner.height);
    }
    
    
    BufferedImage background;    
    /**
     * getter for a static backdrop
     * @return 
     */
    public BufferedImage getBackgroundImage(){
        return background;
    }
    /**
     * Draws tiles to a graphics object according to this object's dimensions
     * @param g
     * @param tiles
     * @return 
     */
    public Graphics2D drawBackground(Graphics2D g, ArrayList<Tile> tiles){
        int i =0;
        for(int y = 0;y<corner.height;y+=Tiles.STANDARD_HEIGHT){
            for(int x = 0;x<corner.width;x+=Tiles.STANDARD_WIDTH){
                g.drawImage(tiles.get(i++).img, null, x, y);
            }
        }
        
        return g;
    }
    
    @Override
    public void update(Observable o, Object arg) { 
        
        if(o instanceof CharacterPiece){
        
            CharacterPiece cp = (CharacterPiece) o;

            if(arg instanceof AttackEvent){
//                ProjectilePiece pjtl = new ProjectilePiece(cp.getHeading(),cp.getPower(),cp.Location);
                add(ProjectilePiece.getProjectile((AttackEvent) arg,cp.Location,cp.getHeading()));
//                System.out.print("\nProjectile Created!");
            }
        }
        
        if(o instanceof GamePiece){
            GamePiece gp = (GamePiece)o;
            movePieceIfAble(gp,new MoveEvent(gp.NextMove));
        }
        
    }
    
    int FrameCount=0;
    public void moveAll(){
        FrameCount++;
        
        if((FrameCount%(Game.FRAMES_PER_SECOND*8) == 0)
                &&(pickupSpawns.size()>0)){
            Dimension rem = pickupSpawns.get(
                    Math.round((float)Math.random()*(pickupSpawns.size()-1)));
            pickupSpawns.remove(rem);
            add(PowerUpPiece.getRandom(rem));
        }
        
        ArrayList<GamePiece> copy = new ArrayList<>(contents);
        for(GamePiece gp: copy){
            gp.move();
        }
        hasMoved = true;
    }

    public void movePieceIfAble(GamePiece gamePiece, MoveEvent moveEvent) {        
        Dimension newLocation = Game.add(gamePiece.Location,moveEvent.vector);
        Dimension oldLocation = new Dimension(gamePiece.Location);
        gamePiece.Location = newLocation;
        for(GamePiece other : contents){
            
            if(other==gamePiece)continue;
            
            if(other.isRigid()){
                if(other.isColliding(gamePiece)){
                    //if new position is colliding
                    gamePiece.Location = oldLocation;
                    if(other.isColliding(gamePiece)){//disallow move if previous position is ok
                        //if old position is colliding too, prefer new position
                        gamePiece.Location = newLocation;
                    }else{
                        //Apply onCollide()
                        other.onCollide(gamePiece);
                        gamePiece.onCollide(other);
                    }
                }
            }else{ //Not Rigid
                if(other.isColliding(gamePiece) &&
                        (other instanceof PowerUpPiece)){
                    
                        //Apply onCollide() to pickups
                        other.onCollide(gamePiece);
                        gamePiece.onCollide(other);
                }
            }
        }
    }
    
    private boolean movesOutOfBounds(GamePiece gamePiece, Dimension newLocation) {
        int top = newLocation.height;
        int bot = newLocation.height + gamePiece.size.height;
        int left = newLocation.width;
        int right = newLocation.width + gamePiece.size.width;
        
        return (top<0)||(bot>corner.height)||(left<0)||(right>corner.width);
    }

    /**
     * Draws all objects to the specified graphics
     * @param g2d
     * @return 
     */
    public Graphics2D drawObjects(Graphics2D g2d)  {
        //copy contents so we can modify it            
        ArrayList<GamePiece> drawables = new ArrayList<>(contents);

        //recursively draws everything in rastor scan order
        while (drawables.size() > 0) {
            GamePiece obj = drawables.remove(0);
            recurseDraw(obj, drawables, g2d);
        }
        
        if (Game.DRAW_DEBUG_LINES) {
            for (GamePiece gp : contents) {
                for (GamePiece ogp : contents) {
                    if (gp != ogp && (gp.getClass() == CharacterPiece.class) && (ogp.getClass() == CharacterPiece.class)) {
                        g2d.drawLine(gp.Location.width, gp.Location.height, ogp.Location.width, ogp.Location.height);
                    }
                }
            }
        }

        return g2d;
    }
    
    /**
     * Draws Objects to a graphic within the specified window
     * @param g2d the graphics canvas
     * @param location top-left corner specification, relative to this map
     * @param size total pixel dimensions of the window
     * @return the modified graphics object
     * @deprecated replaced
     */
    synchronized public Graphics2D drawObjects(Graphics2D g2d, Dimension location, Dimension size)  {
        //copy contents so we can modify it            
        ArrayList<GamePiece> drawables = new ArrayList<>(contents);
        
        
        Rectangle R1 = Game.getRectCollider(location, size);
        
        for(GamePiece gp : contents) {
            if(gp.isColliding(R1)){
                drawables.add(gp);
            } 
        }

        //recursively draws everything in rastor scan order
        while (drawables.size() > 0) {
            GamePiece obj = drawables.remove(0);
            recurseDraw(obj, drawables, g2d);
        }
        
        if (Game.DRAW_DEBUG_LINES) {
            for (GamePiece gp : contents) {
                for (GamePiece ogp : contents) {
                    if (gp != ogp && (gp.getClass() == CharacterPiece.class) && (ogp.getClass() == CharacterPiece.class)) {
                        g2d.drawLine(gp.Location.width, gp.Location.height, ogp.Location.width, ogp.Location.height);
                    }
                }
            }
        }

        return g2d;
    }
    
    
    private void recurseDraw(GamePiece obj, ArrayList<GamePiece> drawables, Graphics2D g2d) {
        /**
         * iterate over all the elements and remove elements that would draw
         * first if the element does draw before object, run this algorithm
         * again to make sure that the remaining objects draw in order
         *
         */
        for (int i = 0; i < drawables.size();) {
            if (drawables.get(i).drawsBefore(obj)) {
                GamePiece first = drawables.remove(i);
                recurseDraw(first, drawables, g2d);
            }
            else  i++;
        }
        /**
         * now that drawables does not contain any elemts that draw before obj
         * we can proceed to draw obj
         *
         */
        obj.draw(g2d);

    }
    
    
    private final BufferedReader source;
    public Map(String sourceFile) throws Exception{
        Dimension d;
        InputStream is = getClass().getResourceAsStream(sourceFile);
        InputStreamReader isr = new InputStreamReader(is);
        
        source = new BufferedReader(isr);
        readMapSource(source);
        
        d = new Dimension(ListLines.get(0).length(),ListLines.size());
        
        /**
         * Initialize data structures
         */
        contents = new ArrayList<>();
        /**
         * initialize background and dimensions
         */
        BufferedImage bg = Game.getSprite("/res/kbr8/Background.bmp");
        corner = new Dimension(bg.getWidth(),bg.getHeight());
        /**
         * Setup backdrop image
         */
        background = Game.getCompatImage(background,corner.width,corner.height);
        Graphics2D gimg = background.createGraphics();
        gimg.drawImage(bg,0,0,null);
        gimg.dispose();
        printout = Game.getCompatImage(printout, corner.width,corner.height);
        /**
         * Create the backdrop tiles
         */
//        ArrayList<Tile> tiles;
//        tiles = new ArrayList();
        /**
         * add tiles to static image
         */
//        int area = d.height*d.width;
//        for(int i = 0; i < area;i++){
//            tiles.add(Tiles.getRandom());
//        }   
//        
        
        //metrics
        System.out.printf("\nMap Size: %d,%d", corner.height, corner.width);
        System.out.printf("\nMap Image Size: %d,%d", background.getHeight(),background.getWidth());
        
        /**
         * Create all the walls
         */
        createWalls();
        source.close();
    }

    private void createWalls() {

        int fileProgress = 0;

        for (int i = wall1.getHeight()/2; (i < corner.height) && (fileProgress < ListLines.size());) {
            String line = ListLines.get(fileProgress);
            int lineProgress = 0;
            for (int j = wall1.getWidth()/2; (j < corner.width) && (lineProgress < line.length());) {

                char c = line.charAt(lineProgress);
                lineProgress += 1;
                switch (c) {
                    case 'w':
                        contents.add(new ObstaclePiece(wall1, new Dimension(j, i)));
                        
                        break;
                    case 'W':
                        contents.add(new ObstaclePiece(wall2, new Dimension(j, i),true));
                        break;
                    case 'B':
                        contents.add(new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                                new Dimension(j, i),PowerUpPiece.POWER_BOUNCE));
                        break;
                    case 'M':
                        contents.add(new PowerUpPiece(PowerUpPiece.POWER_MISSILE_IMG, 
                                new Dimension(j, i),PowerUpPiece.POWER_MISSILE));
                        break;
                    case 'S':
                        contents.add(new PowerUpPiece(PowerUpPiece.POWER_SHIELD_IMG, 
                                new Dimension(j, i),PowerUpPiece.POWER_SHIELD));
                        break;
                    case 'T':
                        contents.add(new PowerUpPiece(PowerUpPiece.POWER_TURRET_IMG, 
                                new Dimension(j, i),PowerUpPiece.POWER_TURRET));
                        break;
                    case 'R':
                        pickupSpawns.add(new Dimension(j,i));
                        break;
                    case 'P':
                        playerSpawns.add(new Dimension(j,i));
                        break;
                            
                }

                j += wall1.getWidth();
            }
            i += wall1.getHeight();
            fileProgress += 1;
        }
    }

    private BufferedImage printout;
    
    private Color transparent = new Color(255, 255, 255, 0);
    
    public final BufferedImage printGameState(){
//        printout = Game.getCompatImage(printout, corner.width, corner.height);
        if(hasMoved){
            Graphics2D g2d = printout.createGraphics();
            g2d.setBackground(transparent);
            //g2d.clearRect(0,0,corner.width, corner.height);// dimens.width, dimens.height);
            g2d.drawImage(this.background, 0, 0, null);
            drawObjects(g2d);
            g2d.dispose();
            hasMoved = false;
        }
        return printout;
    }

    private void readMapSource(BufferedReader source) {
        try {
            while(source.ready()){
                String line = source.readLine();
                ListLines.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cleanUp() {
        ArrayList<GamePiece> copy = new ArrayList<>(contents);
        
        for(GamePiece gp : copy){
            if(gp.disposable()){
                
                remove(gp);
                
                gp.onDispose();
                
                if(gp instanceof CharacterPiece)
                    add(ExplosionAnimation.getExplosion(gp.Location,ExplosionAnimation.LARGE));
                else if(gp instanceof ProjectilePiece)
                    add(ExplosionAnimation.getExplosion(gp.Location,ExplosionAnimation.SMALL));
                else if(gp instanceof PowerUpPiece){
                    pickupSpawns.add(gp.Location);
                }
                else if(gp instanceof ObstaclePiece){
                    wallSpawns.put(gp.Location, Game.FRAMES_PER_SECOND*20);
                }
            }
            
        }
        ArrayList<Dimension> s = new ArrayList(wallSpawns.keySet());
                for(Dimension d : s){
                    int i = wallSpawns.remove(d) -1;
                    if(i<1){
                        add(new ObstaclePiece(wall2, d,true));
                    }else{
                        wallSpawns.put(d, i);
                    }
                }
    }

    public void spawnPlayer(GamePiece player) {
        if(playerSpawns.size()>0){
            player.Location = playerSpawns.get(
                    Math.round((float)Math.random()*
                            (playerSpawns.size()-1)));
            
        }else{
            player.Location = new Dimension(100,100);
        }
        
        Dimension save = new Dimension(player.Location);
        
        for(GamePiece gp : contents){
            if(gp instanceof CharacterPiece)
                if(gp.isColliding(player)){
                    
                    playerSpawns.remove(save);
                    spawnPlayer(player);
                    playerSpawns.add(save);
                    return;
                }
                    
        }
        
        add(player);
    }
}
