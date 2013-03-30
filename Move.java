import java.awt.Point;
import java.util.*;


public class Move{
  

	public int startPointX;
	public int startPointY;
	public int endPointX;
	public int endPointY;
	
	final int BLACKPIECE = -1;
	final int WHITEPIECE = 1;
	final int EMPTYSPOT = 0;
	
	
	public Move(int startX, int startY, int endX, int endY){
		
		startPointX = startX;
		startPointY = startY;
		endPointX = endX;
		endPointY = endY;		
	}
	
	public Point getStartPoint() { return new Point(startPointX, startPointY); }
	
	public Point getEndPoint() { return new Point(endPointX, endPointY); }
	
	//Makes a move based on the information currently stored in move.
	public int[][] makeMove(int[][] gameBoard) { 
			
		
		//2 points, A and B
		//Set A to getStartPoint()
		//set B to getEndPoint()
		//In our game state, we want to update the proper coordinates with the proper numbers
		Point startPoint = this.getStartPoint();
		Point endPoint = this.getEndPoint();
		int colorToKill;
		Boolean captureDir = true; //just always assume they will choose to capture forward given the option for now
		
		//White is moving, original spot now empty, new spot has white pawn
		if(gameBoard[startPoint.x][startPoint.y] == WHITEPIECE){
			gameBoard[startPoint.x][startPoint.y] = EMPTYSPOT;
			gameBoard[endPoint.x][endPoint.y] = WHITEPIECE;
			colorToKill = BLACKPIECE;
		}
		//Otherwise black is moving, original spot empty, new spot has black pawn
		else{
			gameBoard[startPoint.x][startPoint.y] = EMPTYSPOT;
			gameBoard[endPoint.x][endPoint.y] = BLACKPIECE;
			colorToKill = WHITEPIECE;
		}
		
        return aiKillPieces(captureDir, startPoint, endPoint, colorToKill, gameBoard);
    }

	//Called by makeMove
	private int[][] aiKillPieces(Boolean isFwdAtk, Point startPoint, Point endPoint, int colorToKill, int[][] gameBoard) {
      
        Point directionOfAttack = isFwdAtk?(Vector.subtract(endPoint,startPoint)):(Vector.subtract(startPoint,endPoint));
        Point nextStart = isFwdAtk?endPoint:startPoint;
        return killNext(nextStart, directionOfAttack, colorToKill, gameBoard);
    }

    private int[][] killNext(Point nextStart, Point directionOfAttack, int colorToKill, int[][] gameBoard) {
        Point nextPt = Vector.add(nextStart,directionOfAttack);
        if(nextPt.x <= gameBoard.length && nextPt.y <= gameBoard[0].length ) {
            
        	if(gameBoard[nextPt.x][nextPt.y] == EMPTYSPOT ) { return gameBoard; } //done killing
            
            int victim = gameBoard[nextPt.x][nextPt.y];
            
            if(victim == colorToKill) { 
            	gameBoard[nextPt.x][nextPt.x] = EMPTYSPOT;
                return killNext(nextPt, directionOfAttack, colorToKill, gameBoard);
            }
        }
        return gameBoard;
    }
		
		
}
