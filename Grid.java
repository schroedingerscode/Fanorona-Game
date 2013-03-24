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
    private int movesRemaining;
    
    Image bkg;

    static public Point asGridCoor(Point scrCoor) {//{{{
        //Screen coordinates to grid coordinates
        //assumes the grid is at 0,0
        //border is 1 grid square, rounds to nearest grid coor when inexact
        //TODO, figure out why the +50 is needed
        float unroundedX = ((scrCoor.x+50)/SQ_W)-1;
        int x = (int) Math.floor(unroundedX + .5);
        float unroundedY = (scrCoor.y/SQ_H)-1;
        int y = (int) Math.floor(unroundedY + .5);
        return new Point(x,y);
    }//}}}

    public Grid() { //{{{
        super(); 
        //load background image
        //assumes "cherry.png" is in the same directory as the .class files
        ImageIcon ii = new ImageIcon(this.getClass().getResource("cherry.png"));
        bkg = ii.getImage();
        movesRemaining = 50;

        reset(); 
    }//}}}

    public void reset() {//{{{
        clearGridData();
        placePiecesInInitBoardState();
        repaint();
    }//}}}

    //TODO somebody - write the function
    public void movePiece(Point a, Point b) {//{{{
        //returns success or failure
    	movesRemaining--;
    }//}}}

    public int[][] getState() {//{{{
        //returns a 2d array explaining the contents of each grid space
        //to be used by the AI. 1 = player, 0 = empty, -1 = enemy
        int[][] state = new int[9][5]; //x,y
        for(Piece p : playerPieces) {
            state[p.position().x][p.position().y] = 1;
        }
        for(Piece p : enemyPieces) {
            state[p.position().x][p.position().y] = -1;
        }
        
        //check moves remaining
        if(movesRemaining <= 0)
        	this.loseMessage();
        //all other spaces were initialized to 0
        return state;
    }//}}}

    public Piece getPieceAt(Point pt) {//{{{
        //assumes the search won't fail
        for(Piece p : playerPieces) {
            if(p.position() == pt) { return p; }
        }
        for(Piece p : enemyPieces) {
            if(p.position() == pt) { return p; }
        }
        //error, should get here
        return new Piece(new Point(0,0), true);
    }//}}}

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

    private Boolean isUnique(Point coor) {//{{{
        for(Piece p : playerPieces) {
            if(p.position().equals(coor)) { return false; }
        }
        for(Piece p : enemyPieces) {
            if(p.position().equals(coor)) { return false; }
        }
        return true;
    }//}}}

    public Boolean isOnGrid(Point coor) {//{{{
        //Req: is on grid
        //     is unique
        if(coor.x < MIN_GRID_WIDTH_INDEX || coor.x > MAX_GRID_WIDTH_INDEX) {
            return false; 
        }
        if(coor.y < MIN_GRID_HEIGHT_INDEX || coor.y > MAX_GRID_HEIGHT_INDEX) {
            return false; 
        }
        return true;
    }//}}}

    //TODO, tricky because of intermittent diagonals
    public Boolean isAdjacent(Point a, Point b) { return true; }

    //WARNING Not functional until isAdjacent is written & the restriction works
    public Boolean isValidMove(Point a, Point b) {//{{{
        //restrictions:
        //  must capture if possible
        return isAdjacent(a,b) && isOnGrid(b);
    }//}}}

    //TODO someone - write it
    public Boolean isValidDoubleMove(Point a, Point b, java.util.List<Point> prevPositions, int prevDirection) {//{{{
        //restrictions:
        //1- must be same piece as before
        //2- cannot go back to somewhere you've been
        //3- cannot move in the same direction
        //4- must capture
        if(!isValidMove(a,b)) { return false; }


        return false;
    }//}}}

    //TODO someone - write it
    public Boolean canMoveAgain(Point a, java.util.List<Point> prevPositions, int prevDirection) {//{{{
        //per(empty adjacent space) {
        //  if(isValidDoubleMove(stuff)) { return true; }
        //}
        return false;
    }//}}}

    private Boolean addPiece(Boolean isAlly, Point coor) {//{{{
        //no gfx here, the drawing function goes off of stored data
        if(!(isOnGrid(coor) && isUnique(coor))) { return false; }

        Piece p = new Piece(coor, isAlly);
        if(isAlly) {
            playerPieces.add(p);
        } else {
            enemyPieces.add(p);
        }
        return true;
    }//}}}

    private void clearGridData() {//{{{
        playerPieces = new ArrayList<Piece>();
        enemyPieces = new ArrayList<Piece>();
    }//}}}

    private void placePiecesInInitBoardState() {//{{{
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
    }//}}}
    
    public void winMessage()
    {
    	JOptionPane.showMessageDialog(this, "Winner", "Congradulations, You Won!", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void loseMessage()
    {
    	JOptionPane.showMessageDialog(this, "Loser", "You Lose", JOptionPane.PLAIN_MESSAGE);
    }
}
