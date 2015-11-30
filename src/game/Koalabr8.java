/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import static game.Game.controls1;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
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
    
    private int Score = 3;
    private int NUMBER_OF_KOALAS =6;
    public static int FRAMES_PER_SECOND = 100;
    private static double FRAME_PERIOD_MILLIS = (1000/FRAMES_PER_SECOND);
    
    public Koalabr8(){
        URL url = Game.class.getResource("/res/Koala3.map");
        try{
            gameMap = new Map("Koala3.map");
        }catch(Exception e){
            System.out.print(e);
        }
        gameView = new GameView();
        
        for(int i =0; i<=NUMBER_OF_KOALAS; i++){
            CharacterPiece cp = 
                    new CharacterPiece(KoalaImg,playerController,
                            new Dimension(0,0));
            boolean b = gameMap.spawnPlayer(cp);
            if(!b) break;
            else cp.setSpeed(Map.wallDimension.width);
        }
        
    }
    
    public void setMap(String mapname) throws Exception{
        
        gameMap = new Map(mapname);
        while(gameMap.spawnPlayer(new CharacterPiece(KoalaImg,playerController,Game.ZERO_VECTOR)));
        
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
        
        Koalabr8 game = new Koalabr8();
        
        JFrame mainFrame = new JFrame();
        
        mainFrame.setBackground(new Color(64,128,0,255));
        mainFrame.add(game.gameView);
        mainFrame.setSize(new Dimension(1120,580));

        mainFrame.setTitle("Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        
        mainFrame.setFocusable(true);
        mainFrame.setVisible(true); 
        
        mainFrame.addKeyListener(playerController);
        
        game.startThread();
                
    }

    int framePeriod = 0;
    @Override
    public void run() {  
        while(playingMap());
            
        playMenu();
        
        Thread.currentThread().stop();
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
        
    }
    Thread gameThread;
    String[] levels = new String[]{"Koala5.map","Koala3.map","Koala1.map","Koala2.map"};
    String currentMap;
    private void startThread() {
        for(String s : levels){
            try{
                setMap(s);
                currentMap = s;
                gameThread = new Thread(this);
                gameThread.start();
            }
            catch(Exception e){
                System.out.print(e);
            }


            while(gameThread.isAlive());
        }
    }
    private class GameView extends JPanel implements MouseListener{
        
        BufferedImage banner;
        BufferedImage RestartBtn = Game.getCompatSprite("/res/kbr8/Restart.gif");

        public GameView(){
            super();
            banner = Game.getCompatSprite("/res/kbr8/Rescued.gif");
            setSize(new Dimension(gameMap.getCorner().width,
                gameMap.getCorner().height + banner.getHeight()));
            setOpaque(true);
            setBackground(new Color(64,128,0,255));
            this.addMouseListener(this);
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int i = 0; i<gameMap.getScore();i++){
                g.drawImage(KoalaImg, banner.getWidth()+(i*KoalaImg.getWidth()),
                        0, null);
            }
            g.drawImage(RestartBtn, 500, 5, null);
            g.drawImage(gameMap.printGameState(),0,banner.getHeight()+10,null);
            g.drawImage(banner,5,0,null);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.printf("Click location %d,%d\n", e.getPoint().x,e.getPoint().y);
            
            Rectangle r = new Rectangle(500,0,RestartBtn.getWidth(),RestartBtn.getHeight());
            if(r.contains(e.getPoint())){
                resetMap();
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
