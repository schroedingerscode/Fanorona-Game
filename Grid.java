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
    private List<Piece> pieces;
    
    // 0 <= width <= 13 (must be odd)
    // 0 <= height <= 13 (must be odd)
    private int MIN_GRID_WIDTH_INDEX = 0;
    private int MAX_GRID_WIDTH_INDEX;
    private int MIN_GRID_HEIGHT_INDEX = 0;
    private int MAX_GRID_HEIGHT_INDEX;
    static public int SQ_W  = 100; //Square width
    static public int SQ_H  = 100; //Square height
    private int MAXW = (MAX_GRID_WIDTH_INDEX) * SQ_W;
    private int MAXH = (MAX_GRID_HEIGHT_INDEX) * SQ_H;
    
    Image bkg;

    public Point dimensions() {
        return new Point(MAX_GRID_WIDTH_INDEX+1, MAX_GRID_HEIGHT_INDEX+1);
    }
    
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

    public Grid(int rowSize, int colSize) { //{{{
        super(); 
        MAX_GRID_WIDTH_INDEX = rowSize-1;
        MAX_GRID_HEIGHT_INDEX = colSize-1;
        
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

    private Boolean inferOrPromptCaptureDirection(Point a, Point b, Boolean team) {//{{{
        Boolean fwdWorks = canKillFwd(a, b, team);
        Boolean bkwdWorks = canKillBkwd(a, b, team);
        if(fwdWorks && !bkwdWorks) { return true; }
        else if(!fwdWorks && bkwdWorks) { return false; }
        else { //ambiguous situation, prompt
            int reply = JOptionPane.showConfirmDialog(this, "In which direction would you like to capture?\nPress \"Yes\" to capture forward.\nPress \"No\" to capture backwards.", "Capture Forward or Backward?", JOptionPane.YES_NO_OPTION);
            if(reply == JOptionPane.YES_OPTION) {
                return true;
            } else { return false; }
        }
    }//}}}

    public void movePiece(Point a, Point b) {//{{{
        //assumes you already checked that the move was valid
    	Piece p = getPieceAt(a);
        p.move(b);

        Boolean aggressorTeam = p.isPlayer();
        Boolean captureDir = inferOrPromptCaptureDirection(a, b, aggressorTeam);
        killPieces(captureDir, a, b, !aggressorTeam);
        repaint();
    }//}}}

    private void killPieces(Boolean isFwdAtk, Point a, Point b, Boolean killColor) {//{{{
        //attack direction vector
        Point dir = isFwdAtk?(Vector.subtract(b,a)):(Vector.subtract(a,b));
        Point start = isFwdAtk?b:a;
        killNext(start, dir, killColor);
    }//}}}

    private void killNext(Point p, Point dir, Boolean killColor) {//{{{
        Point nextPt = Vector.add(p,dir);
        if(isOnGrid(nextPt)) {
            if(!hasPieceAt(nextPt)) { return; } //done killing
            Piece victim = getPieceAt(nextPt);
            if(victim.isPlayer() == killColor) { 
                kill(victim); 
                killNext(nextPt, dir, killColor);
            }
        }
    }//}}}

    private void kill(Piece p) { pieces.remove(p); }

    public int[][] getState() {//{{{
        //returns a 2d array explaining the contents of each grid space
        //to be used by the AI. 1 = player, 0 = empty, -1 = enemy
        int[][] state = new int[9][5]; //x,y
        for(Piece p : pieces) {
            if(p.isPlayer()) {
                state[p.position().x][p.position().y] = 1;
            } else {
                state[p.position().x][p.position().y] = -1;
            }
        }
        //all other spaces were initialized to 0
        return state;
    }//}}}
	
    //TODO finish
	Boolean winningState(){//{{{
		//Also need to add a turns at 0 condition
		//Can add later - currently part of stateMachine.
		//if(playerPieces.isEmpty() || enemyPieces.isEmpty()){

		//	return true;
		//}
		return false;		
	}//}}}
	
    public Piece getPieceAt(Point pt) {//{{{
        //assumes the search won't fail
        for(Piece p : pieces) {
            if(p.position().equals(pt)) { return p; }
        }
        //error, should not get here, DEBUG code
        System.out.println("ERROR: there is no piece at:" + pt.x + ", " + pt.y);
        System.exit(0);
        return null;//should never get here
    }//}}}

    public Boolean hasPieceAt(Point pt) {//{{{
        for(Piece p : pieces) {
            if(p.position().equals(pt)) { return true; }
        }
        return false;
    }//}}}
    
    private void drawGridLines(Graphics2D g2d) {//{{{
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5)); //line width
        int rowBorder = MAX_GRID_WIDTH_INDEX * SQ_W;
        int colBorder = MAX_GRID_HEIGHT_INDEX * SQ_H;
        
        //drawing columns
        for(int x = MIN_GRID_WIDTH_INDEX + 1; x <= MAX_GRID_WIDTH_INDEX + 1; x++)
        	g2d.draw(new Line2D.Float(x*SQ_W, MIN_GRID_HEIGHT_INDEX+SQ_H, x*SQ_W, colBorder+SQ_H));
        
        //drawing rows
        for(int y = MIN_GRID_WIDTH_INDEX + 1; y <= MAX_GRID_HEIGHT_INDEX + 1; y++)
        	g2d.draw(new Line2D.Float(MIN_GRID_WIDTH_INDEX+SQ_W, y*SQ_H, rowBorder+SQ_W, y*SQ_H));	
   
        g2d.setStroke(new BasicStroke(3)); //line width
    	for(int x = MIN_GRID_WIDTH_INDEX + 1; x <= MAX_GRID_WIDTH_INDEX + 1; x++) {
    		for(int y = MIN_GRID_HEIGHT_INDEX + 1; y <= MAX_GRID_HEIGHT_INDEX + 1; y++) {
    			if((x - y) % 2 == 0) {
    				if((x < MAX_GRID_WIDTH_INDEX + 1) && (y < MAX_GRID_HEIGHT_INDEX + 1))
    					g2d.draw(new Line2D.Float(x*100, y*100, (x+1)*100, (y+1)*100));
    				if((x < MAX_GRID_WIDTH_INDEX + 1) && (y > MIN_GRID_HEIGHT_INDEX + 1))
    					g2d.draw(new Line2D.Float(x*100, y*100, (x+1)*100, (y-1)*100));
    			}
    		}
    	}
    }//}}}
    
    public void paintComponent(Graphics g) {//{{{
        super.paintComponent(g);

        //draw background, 
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bkg, 0, 0, null);
                    
        drawGridLines(g2d);

        //draw pieces based on stored data
        for(Piece p : pieces) {
            p.drawPiece(g2d);
            p.drawPiece(g2d);
        }
    }  //}}}

    private Boolean isUnique(Point coor) {//{{{
        for(Piece p : pieces) {
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
    
    private Boolean isStrongPoint(Point a) {//{{{
        //(odd,odd) or (even,even)
        return (a.x % 2 == 1 && a.y % 2 == 1) || (a.x % 2 == 0 && a.y %2 == 0);
    }//}}}

    //Points where both x and y are odd or even results in 8 adjacent locations
    //Points where x and y are not both odd or even results in only 4 adjacent locations
    //This function only checks for ADJACENT locations (even if out of bounds)
    //isValidMoves checks bounded points
    public Boolean isAdjacent(Point a, Point b) {//{{{
    	List<Point> adjacentPoints = getAdjacentPoints(a);
        for(Point p : adjacentPoints) {
            if(p.equals(b)) { return true; }
        }
    	return false;
    }//}}}

    public List<Point> getAdjacentPoints(Point a) {//{{{
    	List<Point> adjacentPoints = new ArrayList<Point>();
    	adjacentPoints.add(new Point(a.x, a.y + 1));
		adjacentPoints.add(new Point(a.x, a.y - 1));
		adjacentPoints.add(new Point(a.x + 1, a.y));
		adjacentPoints.add(new Point(a.x - 1, a.y));
        //diagonals
        if(isStrongPoint(a)) {
            adjacentPoints.add(new Point(a.x + 1, a.y + 1));
            adjacentPoints.add(new Point(a.x + 1, a.y - 1));
            adjacentPoints.add(new Point(a.x - 1, a.y + 1));
            adjacentPoints.add(new Point(a.x - 1, a.y - 1));
        }
        return adjacentPoints;
    }//}}}

    private Boolean canKillFwd(Point a, Point b, Boolean aggressorTeam) {//{{{
        Point fwd = Vector.subtract(b,a); //forward
        Point tarPt = Vector.add(b,fwd); //target point
        if(!isOnGrid(tarPt) || !hasPieceAt(tarPt)) { return false; }
        if(getPieceAt(tarPt).isPlayer() != aggressorTeam) { return true; }
        return false;
    }//}}}

    private Boolean canKillBkwd(Point a, Point b, Boolean aggressorTeam) {//{{{
        Point bkwd = Vector.subtract(a,b); //backward
        Point tarPt = Vector.add(a,bkwd); //target point
        if(!isOnGrid(tarPt) || !hasPieceAt(tarPt)) { return false; }
        if(getPieceAt(tarPt).isPlayer() != aggressorTeam) { return true; }
        return false;
    }//}}}

    private Boolean canKill(Point a, Point b) {//{{{
        //checks if a piece moving from a to b has any killables neighbours
        Boolean c = getPieceAt(a).isPlayer();
        if(canKillFwd(a,b,c)) { return true; } 
        if(canKillBkwd(a,b,c)) { return true; } 
        return false;
    }//}}}

    public Boolean isValidMove(Point a, Point b) {//{{{
        //restrictions:
        //  must capture if possible
        //isUnique to make sure the space is empty
        System.out.println("Can kill: " + canKill(a,b));
        System.out.println("Adjacent: " + isAdjacent(a,b));
        System.out.println("OnGrid: " + isOnGrid(b));
        System.out.println("Unqiue: " + isUnique(b));
        return canKill(a,b) && isAdjacent(a,b) && isOnGrid(b) && isUnique(b);
    }//}}}

    //!!!WARNING untested
    //public List<Point> getValidMoves(Point a) {//{{{
    //    //returns a list of the valis pt B's when moving from pt A
    //    List<Point> validMoves = getAdjacentPoints(a);
    //    for(Point b : neighbours) {
    //        if(!isValidMove(a, b)) { 
    //            neighbours.remove(b);
    //        }
    //    }
    //    return validMoves;
    //}//}}}

    public Boolean isValidDoubleMove(Point a, Point b, java.util.List<Point> prevPositions, Point prevDirection) {//{{{
        //restrictions:
        //1- must be same piece as before
        //2- must capture
        if(!isValidMove(a,b)) { return false; }
        //3- cannot move in the same direction
        if(Vector.subtract(b,a).equals(prevDirection)) { return false; }
        //4- cannot go back to somewhere you've been
        for(Point p : prevPositions) {
            if(p.equals(b)) { return false; }
        }
        return true;
    }//}}}

    public Boolean canMoveAgain(Point a, java.util.List<Point> prevPositions, Point prevDirection) {//{{{
        List<Point> neighbours = getAdjacentPoints(a);
        for(Point p : neighbours) {
            if(!hasPieceAt(p)) { //empty space
                if(isValidDoubleMove(a, p, prevPositions, prevDirection)) { 
                    return true; 
                }
            }
        }
        return false;
    }//}}}

    private Boolean addPiece(Boolean isAlly, Point coor) {//{{{
        //no gfx here, the drawing function goes off of stored data
        if(!(isOnGrid(coor) && isUnique(coor))) { return false; }

        pieces.add(new Piece(coor, isAlly));
        return true;
    }//}}}
    
    private void clearGridData() { pieces = new ArrayList<Piece>(); }

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

    public void illegalMove() {
    	JOptionPane.showMessageDialog(this, "All moves must capture, move only one space, & remain on the board.", "Invalid move!", JOptionPane.PLAIN_MESSAGE);
    }    
    
    public void illegalDoubleMove() {
    	JOptionPane.showMessageDialog(this, "All double moves must capture, move only one space, remain on the board, not revisit space, & change direction.", "Invalid move!", JOptionPane.PLAIN_MESSAGE);
    }    
    
    public void multiTurnMessage() {
    	JOptionPane.showMessageDialog(this, "Move again or right-click to decline.", "It is still your turn", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void winMessage() {
    	JOptionPane.showMessageDialog(this, "Winner", "Congratulations, You Won!", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void loseMessage() {
    	JOptionPane.showMessageDialog(this, "Loser", "You Lose", JOptionPane.PLAIN_MESSAGE);
    }
}
