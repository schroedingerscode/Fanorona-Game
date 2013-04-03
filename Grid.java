//The (9x5) game board class which stores references to all pieces
//and manages their positions.
//The y-axis is NOT inverted so (1,1) is in the bottom left.
//The x-axis is normal.

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.io.*;

public class Grid extends JPanel{
    private List<Piece> pieces;
    
    static private int MIN_GRID_WIDTH_INDEX = 0;
    static private int MAX_GRID_WIDTH_INDEX; // 0 <= width <= 13 (must be odd)
    static private int MIN_GRID_HEIGHT_INDEX = 0;
    static  private int MAX_GRID_HEIGHT_INDEX; // 0 <= height <= 13 (must be odd)
    public int SQ_W = 100; //Square width including resizeFactor
    public int SQ_H = 100; //Square height including resizeFactor
    private int MAXW = (MAX_GRID_WIDTH_INDEX) * SQ_W;
    private int MAXH = (MAX_GRID_HEIGHT_INDEX) * SQ_H;
    
    double resizeFactor;
    
    Image bkg;

    public Point dimensions() {
        return new Point(MAX_GRID_WIDTH_INDEX+1, MAX_GRID_HEIGHT_INDEX+1);
    }

    public Point asGlobalCoor(double gridX, double gridY) {
        //inverse transform of asGridCoor w/o initial shift
        //allows for decimal inputs
        double numRows = (double) MAX_GRID_HEIGHT_INDEX + 1;
        double oldPtX = gridX - 1;
        double oldPtY = numRows - gridY;
        double W = (double) SQ_W;
        double H = (double) SQ_H;
        double x = (((double) oldPtX + .5) * W);// + 270;
        double y = (((double) oldPtY + .5) * H);// + 0.0;
        return new Point((int) x, (int) y);
    }

    static public Point convertToNewCoorSystem(Point oldPt, int numRows) {
        //convert from top-left at (0,0) to bottom-right at (1,1)
        return new Point(oldPt.x + 1, numRows - oldPt.y);
    }
    
    public Point asGridCoor(Point scrCoor) {//{{{
        //Screen coordinates to grid coordinates
        //TL of grid is at (270,0) as buttons are 120 each *2 + 3 10px spaces
        //shift to origin, scale down, shift to center of piece
        //(SQ_W includes resizeFactor already)
        double W = (double) SQ_W;
        double H = (double) SQ_H;
        double x = (((double)scrCoor.x-270)/W) - .5;
        double y = (((double)scrCoor.y-0.0)/H) - .5;
        Point oldPt = new Point((int) Math.floor(x),(int) Math.floor(y));
        return convertToNewCoorSystem(oldPt, MAX_GRID_HEIGHT_INDEX+1);
    }//}}}

    public Grid(int rowSize, int colSize, double changeFactor) { //{{{
        super(); 
        MAX_GRID_WIDTH_INDEX = rowSize-1;
        MAX_GRID_HEIGHT_INDEX = colSize-1;
        
        SQ_W = (int)(100*changeFactor);
        SQ_H = (int)(100*changeFactor);

        resizeFactor = changeFactor;
        
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

    private int inferCaptureDirection(Point a, Point b, Boolean team) {
        Boolean fwdWorks = canKillFwd(a, b, team);
        Boolean bkwdWorks = canKillBkwd(a, b, team);
        if(fwdWorks && !bkwdWorks) { return 1; }
        else if(!fwdWorks && bkwdWorks) { return -1; }
        else { return 0; }//ambiguous situation
    }
    private Boolean inferOrPromptCaptureDirection(Point a, Point b, Boolean team) {//{{{
        int captureDir = inferCaptureDirection(a, b, team);
        if(captureDir == 1) { return true; }
        else if(captureDir == -1) { return false; }
        //else ambiguous situation, prompt
        int reply = JOptionPane.showConfirmDialog(this, "In which direction would you like to capture?\nPress \"Yes\" to capture forward.\nPress \"No\" to capture backwards.", "Capture Forward or Backward?", JOptionPane.YES_NO_OPTION);
        if(reply == JOptionPane.YES_OPTION) { return true;}
        else { return false; }
    }//}}}

    public String movePaika(Point a, Point b) {//{{{
        //assumes you already checked that the move was valid
    	Piece p = getPieceAt(a);
        p.move(b);
        repaint();
        return ("P " + a.x + " " + a.y + " " + b.x + " " + b.y + " + ");
    }//}}}

    public String movePiece(Point a, Point b, Boolean specifiedDir) {//{{{
        //AI version which does not prompt, infer overrides specified
        //assumes you already checked that the move was valid
    	Piece p = getPieceAt(a);
        p.move(b);
        Boolean aggressorTeam = p.isPlayer();

        int inference = inferCaptureDirection(a, b, aggressorTeam);
        Boolean captureDir = true; //inference == 1
        if(inference == 0) { captureDir = specifiedDir; }
        else if(inference == -1) { captureDir = false; }

        killPieces(captureDir, a, b, !aggressorTeam);
        repaint();
        return ((captureDir?"A":"W") + " " + a.x + " " + a.y + " " + b.x + " " + b.y + " + ");
    }//}}}

    public String movePiece(Point a, Point b) {//{{{
        //player version which prompts
        //assumes you already checked that the move was valid
    	Piece p = getPieceAt(a);
        p.move(b);

        Boolean aggressorTeam = p.isPlayer();
        Boolean captureDir = inferOrPromptCaptureDirection(a, b, aggressorTeam);
        killPieces(captureDir, a, b, !aggressorTeam);
        repaint();
        return ((captureDir?"A":"W") + " " + a.x + " " + a.y + " " + b.x + " " + b.y + " + ");
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
            if(victim.isSacrifice) { return; }
            if(victim.isPlayer() == killColor) { 
                kill(victim); 
                killNext(nextPt, dir, killColor);
            }
        }
    }//}}}

    private void kill(Piece p) { pieces.remove(p); }

    public int[][] getState() {//{{{
        //WARNING - In the new coordinate system (which is not 0-indexed) you must subtract from the piece's coordinates to get the index
        //  1 from x & y when accessing this array
        //returns a 2d array explaining the contents of each grid space
        //to be used by the AI. 1 = player, 0 = empty, -1 = enemy, 2 = zombie
        int[][] state = new int[MAX_GRID_WIDTH_INDEX+1][MAX_GRID_HEIGHT_INDEX+1]; //x,y
        for(Piece p : pieces) {
            if(p.isSacrifice) {
                state[p.position().x-1][p.position().y-1] = 2;
            }
            else if(p.isPlayer()) {
                state[p.position().x-1][p.position().y-1] = 1;
            } else {
                state[p.position().x-1][p.position().y-1] = -1;
            }
        }
        //all other spaces were initialized to 0
        return state;
    }//}}}
	
	boolean checkWinningState(ObjectOutputStream out, Boolean invert){//{{{
		int white = 0;
		int black = 0;
		for(Piece p : pieces) {
			if(p.color)
				white++;
			else
				black++;
		}
		if(!invert) {
			if(white <= 0) {
				try {
					String loseString = "LOSER";
					out.flush();
					out.writeObject(loseString);
					out.flush();
				} catch (Exception e) {}
				loseMessage();
				return true;
			}
			else if(black <= 0) {
				try {
					String winString = "WINNER";
					out.flush();
					out.writeObject(winString);
					out.flush();
				} catch (Exception e) {}
				winMessage();
				return true;
			}
			else
				return false;
		} else {
			if(white <= 0) {
				try {
					String loseString = "WINNER";
					out.flush();
					out.writeObject(loseString);
					out.flush();
				} catch (Exception e) {}
				winMessage();
				return true;
			}
			else if(black <= 0) {
				try {
					String winString = "LOSER";
					out.flush();
					out.writeObject(winString);
					out.flush();
				} catch (Exception e) {}
				loseMessage();
				return true;
			}
			else
				return false;
		}
	}//}}}
	
    public Piece getPieceAt(Point pt) {//{{{
        //assumes the search won't fail
        for(Piece p : pieces) {
            if(p.position().equals(pt)) { return p; }
        }
        //error, should not get here, DEBUG code
        System.err.println("ERROR: there is no piece at:" + pt.x + ", " + pt.y);
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
    					g2d.draw(new Line2D.Float(x*SQ_W, y*SQ_H, (x+1)*SQ_W, (y+1)*SQ_H));
    				if((x < MAX_GRID_WIDTH_INDEX + 1) && (y > MIN_GRID_HEIGHT_INDEX + 1))
    					g2d.draw(new Line2D.Float(x*SQ_W, y*SQ_H, (x+1)*SQ_W, (y-1)*SQ_H));
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

   static public Boolean isOnGrid(Point coor) {//{{{
        //Req: is on grid
        //     is unique
        if(coor.x < MIN_GRID_WIDTH_INDEX+1 || coor.x > MAX_GRID_WIDTH_INDEX+1) {
            return false; 
        }
        if(coor.y < MIN_GRID_HEIGHT_INDEX+1 || coor.y > MAX_GRID_HEIGHT_INDEX+1) {
            return false; 
        }
        return true;
    }//}}}
    
    static private Boolean isStrongPoint(Point a) {//{{{
        //(odd,odd) or (even,even)
        return (a.x % 2 == 1 && a.y % 2 == 1) || (a.x % 2 == 0 && a.y %2 == 0);
    }//}}}

    //Points where both x and y are odd or even results in 8 adjacent locations
    //Points where x and y are not both odd or even results in only 4 adjacent locations
    //This function only checks for ADJACENT locations (even if out of bounds)
    //isValidMoves checks bounded points
    static public Boolean isAdjacent(Point a, Point b) {//{{{
    	List<Point> adjacentPoints = getAdjacentPoints(a);
        for(Point p : adjacentPoints) {
            if(p.equals(b)) { return true; }
        }
    	return false;
    }//}}}

    static public List<Point> getAdjacentPoints(Point a) {//{{{
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
        Piece victimToBe = getPieceAt(tarPt);
        if(victimToBe.isSacrifice) { return false; }
        if(victimToBe.isPlayer() != aggressorTeam) { return true; }
        return false;
    }//}}}

    private Boolean canKillBkwd(Point a, Point b, Boolean aggressorTeam) {//{{{
        Point bkwd = Vector.subtract(a,b); //backward
        Point tarPt = Vector.add(a,bkwd); //target point
        if(!isOnGrid(tarPt) || !hasPieceAt(tarPt)) { return false; }
        Piece victimToBe = getPieceAt(tarPt);
        if(victimToBe.isSacrifice) { return false; }
        if(victimToBe.isPlayer() != aggressorTeam) { return true; }
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
        //technically it should be called isValidCaptureMove
        //restrictions:
        //  must capture if possible
        //isUnique to make sure the space is empty
        //System.out.println("Can kill: " + canKill(a,b));
        //System.out.println("Adjacent: " + isAdjacent(a,b));
        //System.out.println("OnGrid: " + isOnGrid(b));
        //System.out.println("Unqiue: " + isUnique(b));
        return isValidPaikaMove(a,b) && canKill(a,b);
    }//}}}

    public Boolean isValidPaikaMove(Point a, Point b) {
        //on board, adjacent, empty
        return isOnGrid(b) && isAdjacent(a,b) && isUnique(b);
    }

    public Boolean paikaAllowed(Piece a, Point b) {
        //allowed if no capture moves can be made
    	Boolean thisColorsTurn = a.isPlayer();
    	for(Piece p : pieces) {
    		if(p.isPlayer() == thisColorsTurn){
    		List<Point> l = getValidCaptureMoves(p.position());
    		if(l.size() > 0) return false;
    		}
    	}
        return true;
    }

    public List<Point> getValidCaptureMoves(Point a) {//{{{
        //returns a list of the valid pt B's when moving from pt A
        List<Point> neighbours = getAdjacentPoints(a);
        List<Point> validEndPts = new ArrayList<Point>();
        for(Point b : neighbours) {
        	if(isValidMove(a, b)) { 
        		validEndPts.add(b);
            }
        }
        return validEndPts;
    }//}}}

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

    public List<Point> getDoubleMoves(Point a, java.util.List<Point> prevPositions, Point prevDirection) {//{{{
        List<Point> neighbours = getAdjacentPoints(a);
        List<Point> endPts = new ArrayList<Point>();
        for(Point p : neighbours) {
            if(!hasPieceAt(p)) { //empty space
                if(isValidDoubleMove(a, p, prevPositions, prevDirection)) { 
                    endPts.add(p); 
                }
            }
        }
        return endPts;
    }//}}}

    private Boolean addPiece(Boolean isAlly, Point coor) {//{{{
        //no gfx here, the drawing function goes off of stored data
        if(!(isOnGrid(coor) && isUnique(coor))) { return false; }

        pieces.add(new Piece(coor, isAlly, this));
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
	    		addPiece(true, new Point(x+1, y+1));
    		for(int y = (MAX_GRID_HEIGHT_INDEX / 2) + 1; y <= MAX_GRID_HEIGHT_INDEX; y++)
    			addPiece(false, new Point(x+1, y+1));
    		if(x < MAX_GRID_WIDTH_INDEX / 2) {
    			if(count % 2 == 0)
    				addPiece(false, new Point(x+1, MAX_GRID_HEIGHT_INDEX / 2+1));
    			else
    				addPiece(true, new Point(x+1, MAX_GRID_HEIGHT_INDEX / 2+1));
    		}
    		else if(x > MAX_GRID_WIDTH_INDEX / 2) {
    			if(count % 2 == 0)
    				addPiece(true, new Point(x+1, MAX_GRID_HEIGHT_INDEX / 2+1));
    			else
    				addPiece(false, new Point(x+1, MAX_GRID_HEIGHT_INDEX / 2+1));
    		}
    		count++;
    	}
    }//}}}

    public void killSacrifices(Boolean isNowPlayerTurn) {//{{{
    	//have to loop twice to avoid concurrency errors
    	//(can't delete from pieces while iterating through it)
    	List<Integer> toKillList = new ArrayList<Integer>();
    	for(int i = pieces.size() - 1; i > 0; i--) {
    		Piece p = pieces.get(i);
            if(p.isSacrifice) {
                if(p.isPlayer() && isNowPlayerTurn) {
                	toKillList.add(i);
                } else if(!p.isPlayer() && !isNowPlayerTurn) {
                	toKillList.add(i);
                }
            }
        }
    	//the indices are going from largest to smallest just in case
    	for(int i : toKillList) {
    		pieces.remove(i);
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

    public int confirmSacrificePrompt() {
            return JOptionPane.showConfirmDialog(this, "Are you sure you want to sacrifice this piece?", "Confirm Sacrifice", JOptionPane.YES_NO_OPTION);
    }
    
    public void winMessage() {
    	JOptionPane.showMessageDialog(this, "Winner", "Congratulations, You Won!", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void loseMessage() {
    	JOptionPane.showMessageDialog(this, "Loser", "You Lose", JOptionPane.PLAIN_MESSAGE);
    }
}
