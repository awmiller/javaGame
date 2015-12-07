/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import map.KoalaPiece;
import map.KoalaMap;
import static game.Game.controls1;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import map.CharacterPiece;
import map.GamePiece;
import map.Map;

/**
 * Implements Map classes to create a Koalabr8 game with stages represented by
 * differed Map() instances, contains a class GameView for drawing to. Source was
 * adapted from a tank game to fit Koalabr8 game rules.
 * @see Game.Koalabr8.GameView
 * @see Map.Map
 * @author Andros
 */
public class Koalabr8 implements Runnable{
    
    /**
     * Control classes for input management
     */
    public static char[] controls = {'w','a','s','d',' '};
    public static KeyController playerController = new KeyController(controls);
    
    private static final BufferedImage RescuedImage =Game.getCompatSprite("/res/kbr8/Congratulation.gif");
    private static BufferedImage KoalaImg = Game.getCompatSprite("/res/kbr8/Koala_stand.gif");
    private static BufferedImage KoalaDead = Game.getSprite("/res/kbr8/Koala_dead.png");
    
    /**
     * Storage variable to call the appropriate map file on construction
     */
    String currentMap;
    /**
     * The map class used for frame rendering
     */
    private Map gameMap;

    /**
     * Frame Rate and Sampling period
     */
    public static int FRAMES_PER_SECOND = 100;
    private static double FRAME_PERIOD_MILLIS = (1000/FRAMES_PER_SECOND);
    
    /**
     * Local storage of game score and the target score
     */
    private int playerScore = 0;
    private int targetScore=0;
    
    /**
     * Multithreading control variables to control map lifecycle
     */
    public volatile boolean resetMap=false;//signals the game should not transition maps
    public volatile boolean playingMap=true;//continues update loop for a map
    volatile boolean clickToExit = false;//dismisses the endgame screen
    
    /**
     * Signals that the player has scored all koalas
     * @return true if playerScore == targetScore
     */
    public boolean allKoalasSaved(){return playerScore == targetScore;}
    
    /**
     * constructor creates a view for itself and stores the desired map name.
     * @param map the name of a map file
     * @see String[] levels
     */
    public Koalabr8(String map){
        currentMap = map;
        gameView = new GameView(this);        
    }
    
    /**
     * Constructs this instance's map by name and adds koalas, setting targetScore
     * in the process.
     * @param mapname the name of a map file in the Map class path
     * @throws Exception could fail to read the file.
     */
    public void setMap(String mapname) throws Exception{ 
        gameMap = new KoalaMap(mapname);
        targetScore = 0;
        while(gameMap.spawnPlayer(new KoalaPiece(KoalaImg,playerController,Game.ZERO_VECTOR))){
            targetScore++;
        }
    }
    
    //<editor-fold desc="Threaded actions and Frame Rendering">
    
    /**
     * local period control variable
     */
    int framePeriod = 0;
    
    /**
     * Run all frame rendering on this thread
     */
    public Thread FrameRenderingThread;
    
    /**
     * Sets FrameRenderingThread to a new thread so that previous threads are killed.
     * also calls start() so that run() is set.
     */
    private void startThread() {
            try {
                FrameRenderingThread = new Thread(this);
                FrameRenderingThread.start();
                
            } catch (Exception e) {
                System.out.print(e);
            }
    }
    
    
    /**
     * Implementation of Runnable run() plays the map at least once, repeats if
     * resetMap flag is set. Catches exceptions from Map constructor by skipping
     * this map. Contains the main frame rendering loop.
     */
    @Override
    public void run() {  
        
        do {
            playingMap = true;//set flags
            resetMap = false;
            
            try {
                setMap(currentMap);
                
            } catch (Exception ex) {
                Logger.getLogger(Koalabr8.class.getName()).log(Level.SEVERE, null, ex);
                playingMap = false;
            }
            
            //Frame rendering loop
            while (!resetMap && playingMap) {
                playingMap = playingMap();
            }
            System.out.print("Quitting Because of " + (resetMap? "resetMap\n":"playingMap\n"));
            
        }//repreat if flag is set
        while(resetMap);
        
        System.out.print("\n*** Map over! ***\n");
    }
    
    /**
     * Runs a single frame of the map and returns whether the map should continue
     * playing or not.
     * @return true if the next frame should be played.
     */
    private boolean playingMap() {
        long ping = System.currentTimeMillis();
        //move all peices registered and call collisions
        gameMap.moveAll();
        //draw the view
        gameView.repaint();
        //expose post-processing callback
        gameMap.cleanUp();
           
        //calculate frame rate and adjust
        try {
            Thread.sleep((long) framePeriod);//sleep this frame
            //calculate this frequency
            ping = System.currentTimeMillis() - ping;
            framePeriod += (FRAME_PERIOD_MILLIS - ping);
            framePeriod = framePeriod > 0 ? framePeriod : 0;
//            System.out.printf("Frame Rate: %f\n", (float)(1000/((float)framePeriod)));
        } catch (InterruptedException ex) {
            System.out.print(ex);
        }
        
        //determine if there are any koalas left
        boolean continueGame =false;
        for(GamePiece gp : gameMap.getObjects()){
            if(gp instanceof CharacterPiece){
                continueGame=true;
                break;
            }
           
        }
        
        return continueGame;
    }
    
    /**
     * Sleeps the frame rendering thread until a click event is recieved. Click
     * events are registered in the game's view class and reported with clickToExit.
     * This enables the game to display an end-screen message.
     * @see GameView
     * @see clickToExit
     */
    private void playMenu() {
        //reset flag just in case
        clickToExit = false;
        //store a score value
        playerScore = gameMap.getScore();
        //sets a gameView flag to show one of two endgame displays
        gameView.showEndGame(allKoalasSaved());
        
        while(!clickToExit){
            try {
                Thread.sleep(100);
                gameView.repaint();
            } catch (InterruptedException ex) {
                Logger.getLogger(Koalabr8.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
        //reset the flag
        clickToExit = false;
    }
    
    //</editor-fold>
    
    //<editor-fold desc="GameView class and drawing methods">
    //This Koalabr8's instance of GameView
    public GameView gameView;
    /**
     * Implements the view behavior of this model, contains the controllable
     * interface. The game view is rendered form the image created by Map.printGameState()
     * @see Map.Map.printGameState()
     */
    public class GameView extends JPanel implements MouseListener{
        
        /**
         * an image to display at top of screen.
         */
        BufferedImage banner;
        /**
         * Restart gui element
         */
        BufferedImage RestartBtn = Game.getCompatSprite("/res/kbr8/Restart.gif");
        /**
         * A reference to this class's containing class.
         */
        Koalabr8 container;
        /**
         * flags determine which end-screen to show.
         */
        private boolean showWinner=false;
        private boolean showLooser=false;

        public GameView(Koalabr8 game){
            super();
            banner = Game.getCompatSprite("/res/kbr8/Rescued.gif");            
            setSize(new Dimension(680,560));            
            setOpaque(true);
            setBackground(new Color(64,128,0,255));
            this.addMouseListener(this);
            container = game;
        }
        
        @Override
        public void paint(Graphics g) {
            if(gameMap==null) return;
            super.paint(g);
            if(showWinner){
                g.drawImage(RescuedImage, 0, 0, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g.drawString("Click to dismiss!",20,RescuedImage.getHeight()+20);
            }else if(showLooser){
                g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g.drawString(String.format("You Rescued %d/%d Koalas", playerScore,targetScore),20,RescuedImage.getHeight());                
                g.drawString("Click to dismiss!",20,RescuedImage.getHeight()+20);
            }else{
                for(int i = 0; i < gameMap.getScore(); i++) {
                    g.drawImage(KoalaImg, banner.getWidth() + (i * KoalaImg.getWidth()),
                            0, null);
                }
                g.drawImage(RestartBtn, 500, 5, null);
                g.drawImage(gameMap.printGameState(), 0, banner.getHeight() + 10, null);
                g.drawImage(banner,5,0,null);
            }
        }
        
        public void showEndGame(boolean winning){
            showWinner = winning;
            showLooser = !winning;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(gameMap==null) return;
            System.out.printf("Click location %d,%d\n", e.getPoint().x,e.getPoint().y);
            
            Rectangle r = new Rectangle(500,0,RestartBtn.getWidth(),RestartBtn.getHeight());
            if(r.contains(e.getPoint())){
                System.out.print("Resetting Map!\n");
                Koalabr8.this.resetMap = true;
            }
            
            if(showWinner || showLooser){
                container.clickToExit = true;
            }
            
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}

       
        
    }
    
    //</editor-fold>
    
    
    public static void main(String args[]) {
        
        try {
            URL defaultSound = Game.class.getResource("/res/kbr8/Music.mid");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(defaultSound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.print("\n" + e.toString());
        }
        
        JFrame mainFrame = new JFrame();
        
        mainFrame.setBackground(new Color(64,128,0,255));
        mainFrame.setSize(new Dimension(640,580));

        mainFrame.setTitle("Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        
        mainFrame.setFocusable(true);
        mainFrame.setVisible(true); 
        
        mainFrame.addKeyListener(playerController);
        
        
        String[] levels = new String[]{"Koala1.map", "Koala2.map", "Koala3.map", "Koala4.map", "Koala5.map"};
        
        for(String map : levels){
            Koalabr8 game = new Koalabr8(map);
            mainFrame.add(game.gameView);
            game.startThread();
            while(game.playingMap){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Koalabr8.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            game.playMenu();
            mainFrame.remove(game.gameView);
        }
    }

}
