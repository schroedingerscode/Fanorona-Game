//The (9x5) game board class which stores references to all pieces
//and manages their positions.
//The y-axis is inverted so (0,0) is in the top left.
//The x-axis is normal.

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.*;

public class Grid extends JPanel{
    private java.util.List<Piece> playerPieces;
    private java.util.List<Piece> enemyPieces;
    
    private int MIN_GRID_WIDTH_INDEX = 0;
    private int MAX_GRID_WIDTH_INDEX = 8;
    private int MIN_GRID_HEIGHT_INDEX = 0;
    private int MAX_GRID_HEIGHT_INDEX = 4;
    static public int SQ_W  = 100; //Square width
    static public int SQ_H  = 100; //Square height
    private int MAXW = (MAX_GRID_WIDTH_INDEX) * SQ_W;
    private int MAXH = (MAX_GRID_HEIGHT_INDEX) * SQ_H;

    Image bkg;

    private Boolean isUnique(Point coor) {
        for(Piece p : playerPieces) {
            if(p.position().equals(coor)) { return false; }
        }
        for(Piece p : enemyPieces) {
            if(p.position().equals(coor)) { return false; }
        }
        return true;
    }

    //!!!WARNING UNTESTED
    private Boolean isValidPosition(Point coor) {
        //Req: position on grid
        //     unique position
        if(coor.x < MIN_GRID_WIDTH_INDEX || coor.x > MAX_GRID_WIDTH_INDEX) {
            return false; 
        }
        if(coor.y < MIN_GRID_HEIGHT_INDEX || coor.y > MAX_GRID_HEIGHT_INDEX) {
            return false; 
        }

        return isUnique(coor);
    }

    private Boolean addPiece(Boolean isAlly, Point coor) {
        //no gfx here, the drawing function goes off of stored data
        if(!isValidPosition(coor)) { return false; }

        Piece p = new Piece(coor, isAlly);
        if(isAlly) {
            playerPieces.add(p);
        } else {
            enemyPieces.add(p);
        }
        return true;
    }

    private void clearGridData() {
        playerPieces = new ArrayList<Piece>();
        enemyPieces = new ArrayList<Piece>();
    }

    private void placePiecesInInitBoardState() {
        //use resetGrid() for external calls
        //assumes the standard 9x5 board & no addPiece() errors
        //it is highly likely that this requirement will be changed by the
        //instructor in the future 

        //top row (row 0)
        addPiece(false, new Point(0,0));
        addPiece(false, new Point(1,0));
        addPiece(false, new Point(2,0));
        addPiece(false, new Point(3,0));
        addPiece(false, new Point(4,0));
        addPiece(false, new Point(5,0));
        addPiece(false, new Point(6,0));
        addPiece(false, new Point(7,0));
        addPiece(false, new Point(8,0));

        //row 1
        addPiece(false, new Point(0,1));
        addPiece(false, new Point(1,1));
        addPiece(false, new Point(2,1));
        addPiece(false, new Point(3,1));
        addPiece(false, new Point(4,1));
        addPiece(false, new Point(5,1));
        addPiece(false, new Point(6,1));
        addPiece(false, new Point(7,1));
        addPiece(false, new Point(8,1));

        //row 2, mixed colors
        addPiece(false, new Point(0,2));
        addPiece(true, new Point(1,2));
        addPiece(false, new Point(2,2));
        addPiece(true, new Point(3,2));

        addPiece(false, new Point(5,2));
        addPiece(true, new Point(6,2));
        addPiece(false, new Point(7,2));
        addPiece(true, new Point(8,2));

        //row 3
        addPiece(true, new Point(0,3));
        addPiece(true, new Point(1,3));
        addPiece(true, new Point(2,3));
        addPiece(true, new Point(3,3));
        addPiece(true, new Point(4,3));
        addPiece(true, new Point(5,3));
        addPiece(true, new Point(6,3));
        addPiece(true, new Point(7,3));
        addPiece(true, new Point(8,3));

        //row 4
        addPiece(true, new Point(0,4));
        addPiece(true, new Point(1,4));
        addPiece(true, new Point(2,4));
        addPiece(true, new Point(3,4));
        addPiece(true, new Point(4,4));
        addPiece(true, new Point(5,4));
        addPiece(true, new Point(6,4));
        addPiece(true, new Point(7,4));
        addPiece(true, new Point(8,4));
    }

    public int[][] getState() {
        //returns a 2d array explaining the contents of each grid space
        //to be used by the AI. 1 = player, 0 = empty, -1 = enemy
        int[][] state = new int[9][5]; //x,y
        for(Piece p : playerPieces) {
            state[p.position().x][p.position().y] = 1;
        }
        for(Piece p : enemyPieces) {
            state[p.position().x][p.position().y] = -1;
        }
        //all other spaces were initialized to 0
        return state;
    }

    public void reset() {
        clearGridData();
        placePiecesInInitBoardState();
        repaint();
    }

    public Grid() { 
        super(); 

        //load background image
        //assumes "cherry.png" is in the same directory as the .class files
        ImageIcon ii = new ImageIcon(this.getClass().getResource("cherry.png"));
        bkg = ii.getImage();

        reset(); 
    }

    private void drawGridLines(Graphics2D g2d) {//{{{
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5)); //line width
        int xBorder = SQ_W;
        int yBorder = SQ_H;
        //draw column separators
        for(int x = MIN_GRID_WIDTH_INDEX; x < MAX_GRID_WIDTH_INDEX + 1; x++) {
            int globalX = (int) (SQ_W*(x+1));
            g2d.draw(new Line2D.Float(globalX+0, yBorder, globalX,    yBorder + MAXH));
        }
    
        //draw row separators
        for(int y = MIN_GRID_HEIGHT_INDEX; y < MAX_GRID_HEIGHT_INDEX + 1; y++) {
            int globalY = (int) (SQ_H*(y+1));
            g2d.draw(new Line2D.Float(xBorder, globalY + 0, xBorder+MAXW, globalY + 0));
        }

        g2d.setStroke(new BasicStroke(3)); //line width
        //TODO JOSH or NAM, make diagonals NOT hardcoded
        //diagonals, TL to BR
        g2d.draw(new Line2D.Float(100, 300, 300, 500));
        g2d.draw(new Line2D.Float(100, 100, 500, 500));
        g2d.draw(new Line2D.Float(300, 100, 700, 500));
        g2d.draw(new Line2D.Float(500, 100, 900, 500));
        g2d.draw(new Line2D.Float(700, 100, 900, 300));
        
        //diagonals, TR to BL
        g2d.draw(new Line2D.Float(100, 300, 300, 100));
        g2d.draw(new Line2D.Float(100, 500, 500, 100));
        g2d.draw(new Line2D.Float(300, 500, 700, 100));
        g2d.draw(new Line2D.Float(500, 500, 900, 100));
        g2d.draw(new Line2D.Float(700, 500, 900, 300));
    }//}}}

    public void paintComponent(Graphics g) {//{{{
        super.paintComponent(g);

        //draw background, 
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bkg, 0, 0, null);
                    
        drawGridLines(g2d);

        //draw pieces based on stored data
        for(Piece p : playerPieces) {
            p.drawPiece(g2d);
        }
        for(Piece p : enemyPieces) {
            p.drawPiece(g2d);
        }
    }  //}}}

    public Boolean movePiece(Point a, Point b) {
        //returns success or failure

        //TODO somebody - write the function

        return true;
    }
}
