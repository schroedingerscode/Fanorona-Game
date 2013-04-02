import java.awt.Point;
import java.util.*;


public class Move{
  

	public int startPointX;
	public int startPointY;
	public int endPointX;
	public int endPointY;
	public int valueOfMove;
	
	final int BLACKPIECE = -1;
	final int WHITEPIECE = 1;
	final int EMPTYSPOT = 0;
	
	
	public Move(int startX, int startY, int endX, int endY){
		
		startPointX = startX;
		startPointY = startY;
		endPointX = endX;
		endPointY = endY;
		valueOfMove = 0;
	}
	
	public Move(){
	
		startPointX = 0;
		startPointY = 0;
		endPointX = 0;
		endPointY = 0;
		valueOfMove = 0;	
	}
	
	//Copy constructor
	public Move(Move other) {
		
	    this.startPointX = other.startPointX;
	    this.startPointY = other.startPointY;
	    this.endPointX = other.endPointX;
	    this.endPointY = other.endPointY;
	    this.valueOfMove = other.valueOfMove;
	    
	  }
	
	public void setValue(int value) { valueOfMove = value; } 
	
	//Return a point created from the starting coordinates of the move.
	public Point getStartPoint() { return new Point(startPointX, startPointY); }
	
	//Return a point created from the ending coordinates of the move.
	public Point getEndPoint() { return new Point(endPointX, endPointY); }
	
	//Makes a move based on the coordinates currently stored in move.
	public int[][] makeMove(int[][] gameBoard) { 
			
		
		//2 points, A and B...
		//Set A to getStartPoint()
		//Set B to getEndPoint()
		//In our game state, we want to update the proper coordinates with the proper pieces, represented by numbers.
		Point startPoint = this.getStartPoint();
		Point endPoint = this.getEndPoint();
		int colorToKill;
		Boolean captureDir = true; //For now, always assume capture forward when the choice is given.
		
		//White is moving, original spot now empty, new spot has white pawn
		if(gameBoard[startPoint.x-1][startPoint.y-1] == WHITEPIECE){
			gameBoard[startPoint.x-1][startPoint.y-1] = EMPTYSPOT;
			gameBoard[endPoint.x-1][endPoint.y-1] = WHITEPIECE;
			colorToKill = BLACKPIECE;
		}
		//Otherwise black is moving, original spot empty, new spot has black pawn
		else{
			gameBoard[startPoint.x-1][startPoint.y-1] = EMPTYSPOT;
			gameBoard[endPoint.x-1][endPoint.y-1] = BLACKPIECE;
			colorToKill = WHITEPIECE;
		}
		
        return aiKillPieces(captureDir, startPoint, endPoint, colorToKill, gameBoard);
    }

	//Called by makeMove
	private int[][] aiKillPieces(Boolean isFwdAtk, Point startPoint, Point endPoint, int colorToKill, int[][] gameBoard) {
      
        Point directionOfAttack = isFwdAtk?(Vector.subtract(endPoint,startPoint)):(Vector.subtract(startPoint,endPoint));
        Point nextStart = isFwdAtk?endPoint:startPoint;
        return aiKillNext(nextStart, directionOfAttack, colorToKill, gameBoard);
    }
	
	//Called by aiKillPieces and itself
    private int[][] aiKillNext(Point nextStart, Point directionOfAttack, int colorToKill, int[][] gameBoard) {
        
    	Point nextPt = Vector.add(nextStart,directionOfAttack);
    	
        if(Grid.isOnGrid(nextPt) ) {
        	
        	if(gameBoard[nextPt.x-1][nextPt.y-1] == EMPTYSPOT ) { return gameBoard; } //done killing
            
            int victim = gameBoard[nextPt.x-1][nextPt.y-1];
            
            if(victim == colorToKill) { 
            	gameBoard[nextPt.x-1][nextPt.y-1] = EMPTYSPOT;
                return aiKillNext(nextPt, directionOfAttack, colorToKill, gameBoard);
            }
        }
        return gameBoard;
    }
		
		
}
