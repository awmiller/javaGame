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
import map.GamePiece;
import map.Map;

/**
 *
 * @author Andros
 */
public class Koalabr8 implements Runnable{
    public static char[] controls = {'w','a','s','d',' '};
    public KeyController player1Controller = new KeyController(controls);
    
    private ArrayList<GamePiece> Koalas;
    private Map gameMap;
    
    private static BufferedImage KoalaDead = Game.getSprite("/res/kbr8/Koala_dead.png");
    
    private int Score = 3;
    
    public Koalabr8(){
        URL url = Game.class.getResource("/res/Koala1.map");
        try{
            gameMap = new Map("Koala1.map");
        }catch(Exception e){
            
        }
        gameView = new GameView();
        
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
                
    }

    @Override
    public void run() {
        
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
            g.drawImage(gameMap.printGameState(),0,banner.getHeight(),null);
            g.drawImage(banner,0,0,null);
        }
        
    }
    
    
}
