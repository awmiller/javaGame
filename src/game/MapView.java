/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import map.GamePiece;
import map.Map;

/**
 * TODO: this should draw to a buffered image instead of panel so subviews may be drawn
 * TODO: implement minimap with getScaledInstance()
 * TODO: do all work in @Overide paint()
 * @author awmil_000
 */
public class MapView extends JPanel implements Observer{

    private GameController gControl;
    private Map map;
    private Dimension dimens;

    public Dimension getDimens() {
        return dimens;
    }

    MapView(Map gameMap) {
        super();
        map = gameMap;
        dimens = gameMap.getCorner();
        setSize(dimens);
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g.create();
//        drawView(g);
//    }

    public Graphics2D drawView(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d = (Graphics2D) map.drawWorld(g2d);
        g2d = drawObjects(g2d);
        return g2d;
    }

    public MapView(Map m, Dimension d) {
        super();
        map = m;
        dimens = d;
        setSize(d);
    }

    public Graphics2D drawObjects(Graphics2D g2d) {
        //copy contents so we can modify it            
        ArrayList<GamePiece> drawables = new ArrayList<>(map.getObjects());

        //recursively draws everything in rastor scan order
        while (drawables.size() > 0) {
            GamePiece obj = drawables.remove(0);
            recurseDraw(obj, drawables, g2d);
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

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        drawView(g);
    }
    
    
}
