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
    public static int FRAMES_PER_SECOND = 60;
    private static double FRAME_PERIOD_MILLIS = (1000/FRAMES_PER_SECOND);
    
    public Koalabr8(){
        URL url = Game.class.getResource("/res/Koala1.map");
        try{
            gameMap = new Map("Koala2.map");
        }catch(Exception e){
            
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
        
        Thread t = new Thread(game);
        t.start();
                
    }

    int framePeriod = 0;
    @Override
    public void run() {
        while(true){     
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
      }
    }
    
    public GameView gameView;
    private class GameView extends JPanel{
        
        BufferedImage banner;

        public GameView(){
            super();
            banner = Game.getCompatSprite("/res/kbr8/Rescued.gif");
            setSize(new Dimension(gameMap.getCorner().width,
                gameMap.getCorner().height + banner.getHeight()));
            setOpaque(true);
            setBackground(new Color(64,128,0,255));
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int i = 0; i<Score;i++){
                g.drawImage(KoalaDead, banner.getWidth()+(i*KoalaDead.getWidth()),
                        0, null);
            }
            g.drawImage(gameMap.printGameState(),0,banner.getHeight()+10,null);
            g.drawImage(banner,5,0,null);
        }
        
    }
    
    
}
