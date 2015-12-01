/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

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
 *
 * @author Andros
 */
public class Koalabr8 implements Runnable{
    public static char[] controls = {'w','a','s','d',' '};
    public static KeyController playerController = new KeyController(controls);
    
    private ArrayList<GamePiece> Koalas;
    private Map gameMap;
    
    private static BufferedImage KoalaImg = Game.getCompatSprite("/res/kbr8/Koala_stand.gif");
    private static BufferedImage KoalaDead = Game.getSprite("/res/kbr8/Koala_dead.png");
    
    private int Score = 0;
    private int NUMBER_OF_KOALAS =6;
    public static int FRAMES_PER_SECOND = 200;
    private static double FRAME_PERIOD_MILLIS = (1000/FRAMES_PER_SECOND);
    public volatile boolean resetMap=false;
    public volatile boolean playingMap=true;
    private int target=0;
    volatile boolean clickToExit = false;
    
    public boolean allKoalasSaved(){return Score == target;}
    
    public Koalabr8(String map){
        currentMap = map;
        gameView = new GameView(this);        
    }
    
    public void setMap(String mapname) throws Exception{ 
        gameMap = new Map(mapname);
        target = 0;
        while(gameMap.spawnPlayer(new CharacterPiece(KoalaImg,playerController,Game.ZERO_VECTOR))){
            target++;
        }
        
        
    }
    
    private void resetMap() {
        try{
         setMap(currentMap);
        System.out.printf("Map Set to: %s",currentMap); 
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
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
        
        
        String[] levels = new String[]{"superEasy.map","Koala1.map", "Koala2.map", "Koala3.map", "Koala4.map", "Koala5.map"};
        
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

    int framePeriod = 0;
    @Override
    public void run() {  
        
        do {
            playingMap = true;
            resetMap = false;
            try {
                setMap(currentMap);
            } catch (Exception ex) {
                Logger.getLogger(Koalabr8.class.getName()).log(Level.SEVERE, null, ex);
                playingMap = false;
            }
            while (!resetMap && playingMap) {
                playingMap = playingMap();
            }
            System.out.print("Quitting Because of " + (resetMap? "resetMap\n":"playingMap\n"));
            
        }
        while(resetMap);
        
        System.out.print("\n*** Map over! ***\n");
    }
    
    public GameView gameView;

    private boolean playingMap() {
        long ping = System.currentTimeMillis();
        gameMap.moveAll();
        gameView.repaint();
        gameMap.cleanUp();
            
        try {
            Thread.sleep((long) framePeriod);
            ping = System.currentTimeMillis() - ping;
            framePeriod += (FRAME_PERIOD_MILLIS - ping);
            framePeriod = framePeriod > 0 ? framePeriod : 0;
//            System.out.printf("Frame Rate: %f\n", (float)(1000/((float)framePeriod)));
        } catch (InterruptedException ex) {
            System.out.print(ex);
        }
        
        boolean continueGame =false;
        for(GamePiece gp : gameMap.getObjects()){
            if(gp instanceof CharacterPiece){
                continueGame=true;
                break;
            }
           
        }
        
        return continueGame;
    }

    private void playMenu() {
        clickToExit = false;
        Score = gameMap.getScore();
        gameView.showEndGame(allKoalasSaved());
        while(!clickToExit){
            try {
                Thread.sleep(100);
                gameView.repaint();
            } catch (InterruptedException ex) {
                Logger.getLogger(Koalabr8.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
        clickToExit = false;
    }
    Thread gameThread;
    String currentMap;

    private void startThread() {
            try {
                gameThread = new Thread(this);
                gameThread.start();
                
            } catch (Exception e) {
                System.out.print(e);
            }
    }
    

    private static final BufferedImage RescuedImage =Game.getCompatSprite("/res/kbr8/Congratulation.gif");
    private class GameView extends JPanel implements MouseListener{
        
        BufferedImage banner;
        BufferedImage RestartBtn = Game.getCompatSprite("/res/kbr8/Restart.gif");
        Koalabr8 container;
        private boolean showWinner=false;
        private boolean showLooser=false;

        public GameView(Koalabr8 game){
            super();
            banner = Game.getCompatSprite("/res/kbr8/Rescued.gif");
//            setSize(new Dimension(gameMap.getCorner().width,
//                gameMap.getCorner().height + banner.getHeight()));
            
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
                g.drawString(String.format("You Rescued %d/%d Koalas", Score,target),20,RescuedImage.getHeight());                
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
    
    
}
