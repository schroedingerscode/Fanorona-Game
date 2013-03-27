//The (9x5) game board class which stores references to all pieces
//and manages their positions.
//The y-axis is inverted so (0,0) is in the top left.
//The x-axis is normal.

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class Grid extends JPanel{
    private List<Piece> playerPieces;
    private List<Piece> enemyPieces;
    
    // 0 <= width <= 13 (must be odd)
    // 0 <= height <= 13 (must be odd)
    private int MIN_GRID_WIDTH_INDEX = 0;
    private int MAX_GRID_WIDTH_INDEX = 8;
    private int MIN_GRID_HEIGHT_INDEX = 0;
    private int MAX_GRID_HEIGHT_INDEX = 4;
    static public int SQ_W  = 100; //Square width
    static public int SQ_H  = 100; //Square height
    private int MAXW = (MAX_GRID_WIDTH_INDEX) * SQ_W;
    private int MAXH = (MAX_GRID_HEIGHT_INDEX) * SQ_H;
    
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

        reset(); 
    }//}}}

    public void reset() {//{{{
        clearGridData();
        placePiecesInInitBoardState();
        repaint();
    }//}}}

    private Boolean promptCaptureDirection() {
    	JOptionPane.showMessageDialog(this, "TODO: Ask user whether they'd like to capture fwds or bkwds or infer it. Assuming forward for now.", "Capture Fwd or Bkwd?", JOptionPane.PLAIN_MESSAGE);
        return true;
    }

    public void movePiece(Point a, Point b) {//{{{
        //assumes you already checked that the move was valid
    	Piece p = getPieceAt(a);
        p.move(b);
        killPieces(promptCaptureDirection(), !p.isPlayer(), a, b);
        repaint();
    }//}}}

    private void killPieces(Boolean isFwdAtk, Boolean killColor, Point a, Point b) {
        //attack direction vector
        Point dir = (isFwdAtk)?(Vector.subtract(b,a)):(Vector.subtract(a,b));
        Point start = (isFwdAtk)?b:a;
        killNext(start, dir, killColor);
    }

    private void killNext(Point p, Point dir, Boolean killColor) {
        Point nextPt = Vector.add(p,dir);
        if(isOnGrid(nextPt)) {
            Piece victim = getPieceAt(nextPt);
            if(victim.isPlayer() == killColor) { 
                kill(victim); 
                killNext(nextPt, dir, killColor);
            }
        }
    }

    private void kill(Piece p) {
        if(p.isPlayer()) { playerPieces.remove(p); }
        else { enemyPieces.remove(p); }
    }

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
        //all other spaces were initialized to 0
        return state;
    }//}}}
	
	Boolean winningState(){//{{{
		
		//Also need to add a turns at 0 condition
		//Can add later - currently part of stateMachine.
		if(playerPieces.isEmpty() || enemyPieces.isEmpty()){

			return true;
		}
		return false;		
	
	}//}}}
	
    public Piece getPieceAt(Point pt) {//{{{
        //assumes the search won't fail
        for(Piece p : playerPieces) {
            if(p.position().equals(pt)) { return p; }
        }
        for(Piece p : enemyPieces) {
            if(p.position().equals(pt)) { return p; }
        }
        //error, should not get here
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
            g2d.draw(new Line2D.Float(globalX+0, yBorder, globalX, yBorder + MAXH));
        }
    
        //draw row separators
        for(int y = MIN_GRID_HEIGHT_INDEX; y < MAX_GRID_HEIGHT_INDEX + 1; y++) {
            int globalY = (int) (SQ_H*(y+1));
            g2d.draw(new Line2D.Float(xBorder, globalY + 0, xBorder+MAXW, globalY + 0));
        }

        g2d.setStroke(new BasicStroke(3)); //line width
        //TODO JOSH or NAM, make diagonals NOT hardcoded
        //working on this:
/*        if(MAX_GRID_WIDTH_INDEX > MAX_GRID_HEIGHT_INDEX) {
    		for(int x = MIN_GRID_WIDTH_INDEX + 1; x <= (MAX_GRID_WIDTH_INDEX+1) / 2; x += 2) {
    			g2d.draw(new Line2D.Float(x*100, (MIN_GRID_HEIGHT_INDEX+1)*100, (x+(MAX_GRID_WIDTH_INDEX/2))*100, (MAX_GRID_HEIGHT_INDEX+1)*100));
    		}
    	}
        
        else if(MAX_GRID_WIDTH_INDEX == MAX_GRID_HEIGHT_INDEX) {
        	for(int x = MIN_GRID_WIDTH_INDEX + 1; x < MAX_GRID_WIDTH_INDEX / 2; x += 2) {
    			g2d.draw(new Line2D.Float(x*100, MIN_GRID_HEIGHT_INDEX, (x+4)*100, MAX_GRID_HEIGHT_INDEX));
    		}
        }
        
        else {
        	for(int x = MIN_GRID_WIDTH_INDEX + 1; x < MAX_GRID_WIDTH_INDEX / 2; x += 2) {
    			g2d.draw(new Line2D.Float(x*100, MIN_GRID_HEIGHT_INDEX, (x+4)*100, MAX_GRID_HEIGHT_INDEX));
    		}
        }*/
        
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

    //Points where both x and y are odd or even results in 8 adjacent locations
    //Points where x and y are not both odd or even results in only 4 adjacent locations
    //This function only checks for ADJACENT locations (even if out of bounds)
    //isValidMoves checks bounded points
    public Boolean isAdjacent(Point a, Point b) {
    	List<Point> adjacentPoints = new ArrayList<Point>();
    	adjacentPoints.add(new Point(a.x, a.y + 1));
		adjacentPoints.add(new Point(a.x, a.y - 1));
		adjacentPoints.add(new Point(a.x + 1, a.y));
		adjacentPoints.add(new Point(a.x - 1, a.y));
    		adjacentPoints.add(new Point(a.x + 1, a.y + 1));
		adjacentPoints.add(new Point(a.x + 1, a.y - 1));
		adjacentPoints.add(new Point(a.x - 1, a.y + 1));
		adjacentPoints.add(new Point(a.x - 1, a.y - 1));
    	if((a.x % 2 == 1 && a.y % 2 == 1) || (a.x % 2 == 0 && a.y %2 == 0)) {    		
    		for(int n = 0; n < adjacentPoints.size(); n++) {
    			if(adjacentPoints.get(n).x == b.x && adjacentPoints.get(n).y == b.y)
    				return true;
    		}
    	}
    	else {
    		for(int n = 0; n < adjacentPoints.size() / 2; n++) {
    			if(adjacentPoints.get(n).x == b.x && adjacentPoints.get(n).y == b.y)
    				return true;
    		}
    	}
    	return false;
    }

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
        Boolean canMoveAgain = false;
        //per(empty adjacent space) {
        //  if(isValidDoubleMove(stuff)) { return true; }
        //}
        return canMoveAgain;
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

        //soft coding of adding pieces
    	int count = 0;
    	for(int x = MIN_GRID_WIDTH_INDEX; x <= MAX_GRID_WIDTH_INDEX; x++) {
    		for(int y = MIN_GRID_HEIGHT_INDEX; y < MAX_GRID_HEIGHT_INDEX / 2; y++)
	    		addPiece(false, new Point(x, y));
    		for(int y = (MAX_GRID_HEIGHT_INDEX / 2) + 1; y <= MAX_GRID_HEIGHT_INDEX; y++)
    			addPiece(true, new Point(x, y));
    		if(x < MAX_GRID_WIDTH_INDEX / 2) {
    			if(count % 2 == 0)
    				addPiece(false, new Point(x, MAX_GRID_HEIGHT_INDEX / 2));
    			else
    				addPiece(true, new Point(x, MAX_GRID_HEIGHT_INDEX / 2));
    		}
    		else if(x > MAX_GRID_WIDTH_INDEX / 2) {
    			if(count % 2 == 0)
    				addPiece(true, new Point(x, MAX_GRID_HEIGHT_INDEX / 2));
    			else
    				addPiece(false, new Point(x, MAX_GRID_HEIGHT_INDEX / 2));
    		}
    		count++;
    	}
    }//}}}
    
    public void multiTurnMessage() {
    	JOptionPane.showMessageDialog(this, "Move Again", "It is still your turn", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void winMessage() {
    	JOptionPane.showMessageDialog(this, "Winner", "Congratulations, You Won!", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void loseMessage() {
    	JOptionPane.showMessageDialog(this, "Loser", "You Lose", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void illegalMove() {
    	JOptionPane.showMessageDialog(this, "No!", "Invalid move!", JOptionPane.PLAIN_MESSAGE);
    }    
}
