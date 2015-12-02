/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import game.Game;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import map.ExitPiece;
import map.LockSwitchPieces;
import map.Map;
import map.ObstaclePiece;
import map.SawPiece;
import map.StonePiece;
import map.TnTPiece;

/**
 *
 * @author awmil_000
 */
public class KoalaMap extends Map {

    public KoalaMap(String mapname) throws Exception {
        super(mapname);
    }

    static BufferedImage BotCenterWall;     // ','
    static BufferedImage LeftCenterWall;    // ']'
    static BufferedImage TopCenterWall;     // '''
    static BufferedImage RightCenterWall;   // '['
    static BufferedImage TopRightWall;      // 'L'
    static BufferedImage BotRightWall;      // '<'
    static BufferedImage BotLeftWall;       // '>'
    static BufferedImage TopLeftWall;       // 'J'
    static BufferedImage InvTWall;          // 'w'
    static BufferedImage RightTWall;        // '}'
    static BufferedImage TWall;             // 'T'
    static BufferedImage LeftTWall;         // '{'
    static BufferedImage CenterWall;        // 'o'
    static BufferedImage allDirWall;        // '+'
    static BufferedImage TopBotWall;        // '|'
    static BufferedImage leftRightWall;     // '='

    public static final Dimension wallDimension = new Dimension(40, 40);

    static {
        int wd = wallDimension.width;
        int ht = wallDimension.height;
        BufferedImage wallStrip = Game.getCompatSprite("/res/kbr8/Wall_tiles.png");
        BotCenterWall = wallStrip.getSubimage(0, 0, wd, ht);
        LeftCenterWall = wallStrip.getSubimage(1 * wd, 0, wd, ht);
        TopCenterWall = wallStrip.getSubimage(2 * wd, 0, wd, ht);
        RightCenterWall = wallStrip.getSubimage(3 * wd, 0, wd, ht);
        TopRightWall = wallStrip.getSubimage(0 * wd, 1 * ht, wd, ht);
        BotRightWall = wallStrip.getSubimage(1 * wd, 1 * ht, wd, ht);
        BotLeftWall = wallStrip.getSubimage(2 * wd, 1 * ht, wd, ht);
        TopLeftWall = wallStrip.getSubimage(3 * wd, 1 * ht, wd, ht);
        InvTWall = wallStrip.getSubimage(0 * wd, 2 * ht, wd, ht);
        RightTWall = wallStrip.getSubimage(1 * wd, 2 * ht, wd, ht);
        TWall = wallStrip.getSubimage(2 * wd, 2 * ht, wd, ht);
        LeftTWall = wallStrip.getSubimage(3 * wd, 2 * ht, wd, ht);
        CenterWall = wallStrip.getSubimage(0 * wd, 3 * ht, wd, ht);
        allDirWall = wallStrip.getSubimage(1 * wd, 3 * ht, wd, ht);
        TopBotWall = wallStrip.getSubimage(2 * wd, 3 * ht, wd, ht);
        leftRightWall = wallStrip.getSubimage(3 * wd, 3 * ht, wd, ht);
    }

    protected ArrayList<Dimension> YellowLocks;
    protected ArrayList<Dimension> RedLocks;
    protected ArrayList<Dimension> BlueLocks;

    private int numLocks = 0;
    private int numSwitches = 0;

    @Override
    public BufferedImage createBackground() {
        /**
         * initialize background and dimensions
         */
        BufferedImage bg = Game.getCompatSprite("/res/kbr8/Background.bmp");
        return bg;
    }

    @Override
    public void createWalls() {

        this.BlueLocks = new ArrayList<>();
        this.RedLocks = new ArrayList<>();
        this.YellowLocks = new ArrayList<>();
        int fileProgress = 0;

        for (int i = wallDimension.height / 2; (i < corner.height) && (fileProgress < ListLines.size());) {
            String line = ListLines.get(fileProgress);
            int lineProgress = 0;
            for (int j = wallDimension.width / 2; (j < corner.width) && (lineProgress < line.length());) {

                char c = line.charAt(lineProgress);
                lineProgress += 1;
                switch (c) {

                    //Force Blank Space
                    case '.':
                        break;

                    case 'P':
                        playerSpawns.add(new Dimension(j, i));
                        break;
                    case 'H':
                        add(new SawPiece(new Dimension(j, i), SawPiece.HORIZONTAL));
                        break;
                    case 'V':
                        add(new SawPiece(new Dimension(j, i), SawPiece.VERTICAL));
                        break;

                    case 't':
                        contents.add(new TnTPiece(new Dimension(j, i)));
                        break;
                    case 'y':
                        numLocks++;
                        YellowLocks.add(new Dimension(j, i));
                        break;
                    case 'r':
                        RedLocks.add(new Dimension(j, i));
                        numLocks++;
                        break;
                    case 'b':
                        BlueLocks.add(new Dimension(j, i));
                        numLocks++;
                        break;

                    case 'R':
                    case 'Y':
                    case 'B':
                        numSwitches++;
                        contents.add(new LockSwitchPieces(new Dimension(j, i), c, this));
                        break;
                    case 's':
                        contents.add(new StonePiece(new Dimension(j, i), this));
                        break;
                    case 'e':
                        Dimension dim = new Dimension(j, i);
                        Exits.add(dim);
                        contents.add(new ExitPiece(dim));
                        break;
//      BotCenterWall;     // 
                    case ',':
                        contents.add(new ObstaclePiece(BotCenterWall, new Dimension(j, i)));
                        break;
//      LeftCenterWall;    // 
                    case ']':
                        contents.add(new ObstaclePiece(LeftCenterWall, new Dimension(j, i)));
                        break;
//      TopCenterWall;     // 
                    case '\'':
                        contents.add(new ObstaclePiece(TopCenterWall, new Dimension(j, i)));
                        break;
//      RightCenterWall;   // 
                    case '[':
                        contents.add(new ObstaclePiece(RightCenterWall, new Dimension(j, i)));
                        break;
//      TopRightWall;      // 
                    case 'L':
                        contents.add(new ObstaclePiece(TopRightWall, new Dimension(j, i)));
                        break;
//      BotRightWall;      // 
                    case '<':
                        contents.add(new ObstaclePiece(BotRightWall, new Dimension(j, i)));
                        break;
//      BotLeftWall;       // 
                    case '>':
                        contents.add(new ObstaclePiece(BotLeftWall, new Dimension(j, i)));
                        break;
//      TopLeftWall;       // 
                    case 'J':
                        contents.add(new ObstaclePiece(TopLeftWall, new Dimension(j, i)));
                        break;
//      InvTWall;          // 
                    case 'w':
                        contents.add(new ObstaclePiece(InvTWall, new Dimension(j, i)));
                        break;
//      RightTWall;        // 
                    case '}':
                        contents.add(new ObstaclePiece(RightTWall, new Dimension(j, i)));
                        break;
//      TWall;             // 
                    case 'T':
                        contents.add(new ObstaclePiece(TWall, new Dimension(j, i)));
                        break;
//      LeftTWall;         // 
                    case '{':
                        contents.add(new ObstaclePiece(LeftTWall, new Dimension(j, i)));
                        break;
//      CenterWall;        // 
                    case 'o':
                        contents.add(new ObstaclePiece(CenterWall, new Dimension(j, i)));
                        break;
//      allDirWall;        // 
                    case '+':
                        contents.add(new ObstaclePiece(allDirWall, new Dimension(j, i)));
                        break;
//      TopBotWall;        // 
                    case '|':
                        contents.add(new ObstaclePiece(TopBotWall, new Dimension(j, i)));
                        break;
//      leftRightWall;     // 
                    case '=':
                        contents.add(new ObstaclePiece(leftRightWall, new Dimension(j, i)));
                        break;

                }

                j += wallDimension.width;
            }
            i += wallDimension.height;
            fileProgress += 1;
        }

    }

}
