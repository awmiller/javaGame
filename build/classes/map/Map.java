/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.AttackEvent;
import game.Game;
import game.MapView;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author awmil_000
 */
public class Map implements Observer{
    private boolean hasMoved;
    
    ArrayList<GamePiece> contents;
    ArrayList<String> ListLines = new ArrayList<>();
    
    static BufferedImage wall1 = (BufferedImage) Game.getSprite("/res/Wall1.gif");
    static BufferedImage wall2 = (BufferedImage) Game.getSprite("/res/Wall2.gif");
    
    static{
        int wd = wall1.getWidth();
        int ht = wall1.getHeight();
        wall1 = Game.getCompatImage(wall1, wd,ht);
        wall2 = Game.getCompatImage(wall2, wd,ht);
        Graphics2D g1 = wall1.createGraphics();
        g1.drawImage((BufferedImage) Game.getSprite("/res/Wall1.gif"), 0,0, null);
        g1.dispose();
        
        Graphics2D g2 = wall2.createGraphics();
        g2.drawImage((BufferedImage) Game.getSprite("/res/Wall2.gif"), 0,0, null);
        g2.dispose();
    }
    
    private final BufferedReader source;
    
    public GamePiece add(GamePiece go){
        contents.add(go);
        go.addObserver(this);
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
    
    public Map(String sourceFile) throws Exception{
        Dimension d;
        source = new BufferedReader(new FileReader(sourceFile));
        readMapSource(source);
        
        d = new Dimension(ListLines.get(0).length(),ListLines.size());
        
        /**
         * Initialize data structures
         */
        contents = new ArrayList<>();
        corner = new Dimension(d.width*wall1.getWidth(),d.height*wall1.getHeight());
        printout = Game.getCompatImage(printout, corner.width,corner.height);
        /**
         * Create the backdrop tiles
         */
        ArrayList<Tile> tiles;
        tiles = new ArrayList();
        /**
         * add tiles to static image
         */
        int area = d.height*d.width;
        for(int i = 0; i < area;i++){
            tiles.add(Tiles.getRandom());
        }   
        
        /**
         * Setup backdrop image
         */
        background = Game.getCompatImage(background,corner.width,corner.height);
        Graphics2D gimg = background.createGraphics();
        drawBackground(gimg, tiles);
        //metrics
        System.out.printf("Map Size: %d,%d", corner.height, corner.width);
        System.out.printf("Map Image Size: %d,%d", background.getHeight(),background.getWidth());
        
        /**
         * Create all the walls
         */
        createWalls();
    }

    @Override
    public void update(Observable o, Object arg) { 
        
        if(o instanceof CharacterPiece){
        
            CharacterPiece cp = (CharacterPiece) o;

            if(arg instanceof AttackEvent){
//                ProjectilePiece pjtl = new ProjectilePiece(cp.getHeading(),cp.getPower(),cp.Location);
                add(ProjectilePiece.getProjectile((AttackEvent) arg,cp.Location,cp.getHeading()));
                System.out.print("\nProjectile Created!");
            }
        }
        
        if(o instanceof GamePiece){
            GamePiece gp = (GamePiece)o;
            movePieceIfAble(gp,new MoveEvent(gp.NextMove));
        }
        
    }
    
    public void moveAll(){
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

    private void createWalls() {

        int fileProgress = 0;

        for (int i = 0; (i < corner.height) && (fileProgress < ListLines.size());) {
            String line = ListLines.get(fileProgress);
            int lineProgress = 0;
            for (int j = 0; (j < corner.width) && (lineProgress < line.length());) {

                char c = line.charAt(lineProgress);
                lineProgress += 1;
                switch (c) {
                    case 'w':
                        contents.add(new ObstaclePiece(wall1, new Dimension(j, i)));
                        break;
                    case 'W':
                        contents.add(new ObstaclePiece(wall2, new Dimension(j, i)));
                        break;
                    case 'B':
                        contents.add(new PowerUpPiece(PowerUpPiece.POWER_BOUNCE_IMG, 
                                new Dimension(j, i),PowerUpPiece.POWER_BOUNCE));
                        break;
                    case 'M':
                        contents.add(new ObstaclePiece(wall2, new Dimension(j, i)));
                        break;
                    case 'S':
                        contents.add(new ObstaclePiece(wall2, new Dimension(j, i)));
                        break;
                    case 'T':
                        contents.add(new ObstaclePiece(wall2, new Dimension(j, i)));
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
    
    public final BufferedImage printObjectsImage(){
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
                contents.remove(gp);
                gp.onDispose();
                if(gp instanceof CharacterPiece)
                    add(ExplosionAnimation.getExplosion(gp.Location,ExplosionAnimation.LARGE));
                else if(gp instanceof ProjectilePiece)
                    add(ExplosionAnimation.getExplosion(gp.Location,ExplosionAnimation.SMALL));
            }
        }
    }
}
