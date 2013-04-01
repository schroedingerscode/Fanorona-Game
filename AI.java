import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AI {
    AI() {}
    
    private final int BLACKPIECE = -1;
    private final int WHITEPIECE = 1;
    private final int EMPTYSPOT = 0;
	public int MAX_GRID_WIDTH_INDEX;
	public int MAX_GRID_HEIGHT_INDEX;
	
    Boolean gameOver(int[][] gameState){

	// Place holder, code written just have to meld things together
	// during our next meeting
	return false;
	
    }
    
    void setBounds(int rowSize, int columnSize){
    	
    	MAX_GRID_WIDTH_INDEX = rowSize;
    	MAX_GRID_HEIGHT_INDEX = columnSize;
    	
    }
    
	int evaluateBoard(int[][] gameState){
		int value = 0;
		
		for (int x = 0; x < MAX_GRID_WIDTH_INDEX; x++){ 
			for (int y = 0; y < MAX_GRID_HEIGHT_INDEX; y++){ 
				if(gameState[x][y] == 1) value++;
				if(gameState[x][y] == -1) value--;
			}
		}
		return value;
	}	
	
	public Boolean aiIsOnGrid(Point move) {
		
        if(move.x < 1 || move.x > MAX_GRID_WIDTH_INDEX+1) return false;
        
        if(move.y < 1 || move.y > MAX_GRID_HEIGHT_INDEX+1) return false; 
        
        return true;
    }
	
	private Boolean aiIsStrongPoint(Point a){
		
        return (a.x % 2 == 1 && a.y % 2 == 1) || (a.x % 2 == 0 && a.y %2 == 0);
    }

    ArrayList<Move> getValidMoves(int[][] gameState) {//{{{
    	
    	
    	//For our current board, we want to find all of the black pawns on the board.
    	//Black pawns = ArrayList<Point> ... created from x y coordinates of board.
    	ArrayList<Point> blackPawnLocations = new ArrayList<Point>();
    	ArrayList<Move> validMoves = new ArrayList<Move>();
    	
    	for (int x = 0; x < MAX_GRID_WIDTH_INDEX; x++){ 
			for (int y = 0; y < MAX_GRID_HEIGHT_INDEX; y++){ 
				
				if(gameState[x][y] == BLACKPIECE){
					
					blackPawnLocations.add(new Point(x + 1,y + 1));
					
				}
			}
		}        	
    	
    	//For each possible move, there are at least 4 adjacent squares (possibly 8).
    	//For the possible squares, we make sure that the new move is several things.
    	//1.) Does the piece moving have killable neighbors?
    	//2.) Are the coordinates the piece is moving to actually on the board?
    	//If it is these first two things, we need one final check.
    	//3.) Is the spot being moved to empty?
    	//If all these checks pass, we have found a valid move and add it to our valid move list.
    	
    	int movingColor = 0;
    	int fwdColor = 0;
    	int bkwdColor = 0;
    	
	    for (Point startLocation: blackPawnLocations){
	    	
	    	//We already know it's adjacent
	    	//Check if it is on grid
	    	//Check if it has killable neighbors
	    	//Check if it's an empty spot
	    	
	    	
	    	//Case 1 ... x , y + 1
	    	//If it's on the grid and the destination spot is empty...
	    	if( aiIsOnGrid(new Point(startLocation.x, startLocation.y + 1)) 
	    	&& (gameState[startLocation.x-1][startLocation.y] == EMPTYSPOT) ){
	    		
	    		//We want to find if it has killable neighbors, so we need some points.
	    		Point newLocation = new Point(startLocation.x, startLocation.y + 1);
	    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
    	        
    	        //This is the color of the piece making a move.
    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
    	        
    	        //Make sure a forward move is on the grid.
    	        if(aiIsOnGrid(fwdTarPt)){
    	        	
    	        	//This is the piece that is in the destination spot, possibly empty.
    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
    	        	
    	        	//Can kill in this direction, passed all valid move checks. 
    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
	    	        
    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
    	        	}
    	        }	    		
    	        else if(aiIsOnGrid(bkwdTarPt)){
	    			
		    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
		    		
		    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
		    			
		    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
		        	}
	    		}
	    	}
	    	//Case 2 ... x , y - 1
	    	if( aiIsOnGrid(new Point(startLocation.x, startLocation.y - 1)) 
	    	&& (gameState[startLocation.x-1][startLocation.y-2] == EMPTYSPOT) ){
	    		
	    		Point newLocation = new Point(startLocation.x, startLocation.y - 1);
	    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
    	        
    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
    	        
    	        if(aiIsOnGrid(fwdTarPt)){
    	        	
    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
    	        	
    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
	    	        
    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
    	        	}
    	        }
	    		
	    		if(aiIsOnGrid(bkwdTarPt)){
	    			
		    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
		    		
		    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
		    			
		    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
		        	}
	    		}
	    	}
	    	//Case 3 ... x + 1 , y
	    	if( aiIsOnGrid(new Point(startLocation.x + 1, startLocation.y)) 
	    	&& (gameState[startLocation.x][startLocation.y-1] == EMPTYSPOT) ){
	    		
	    		Point newLocation = new Point(startLocation.x + 1, startLocation.y);
	    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
    	        
    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
    	        
    	        if(aiIsOnGrid(fwdTarPt)){
    	        	
    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
    	        	
    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
	    	        
    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
    	        	}
    	        }
	    		
	    		if(aiIsOnGrid(bkwdTarPt)){
	    			
		    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
		    		
		    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
		    			
		    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
		        	}
	    		}
	    	}
	    	//Case 4 ... x - 1 , y
	    	if( aiIsOnGrid(new Point(startLocation.x - 1, startLocation.y)) 
	    	&& (gameState[startLocation.x - 2][startLocation.y-1] == EMPTYSPOT) ){
	    		
	    		Point newLocation = new Point(startLocation.x - 1, startLocation.y);
	    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
    	        
    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
    	        
    	        if(aiIsOnGrid(fwdTarPt)){
    	        	
    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
    	        	
    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
	    	        
    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
    	        	}
    	        }
	    		
	    		if(aiIsOnGrid(bkwdTarPt)){
	    			
		    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
		    		
		    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
		    			
		    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
		        	}
	    		}
	    	}
	    	//Strong point cases
	    	if( aiIsStrongPoint(startLocation) ){
	    		
	    		//Case 5 ... x + 1, y + 1
	    		if( aiIsOnGrid(new Point(startLocation.x + 1, startLocation.y + 1)) 
		    	&& (gameState[startLocation.x][startLocation.y] == EMPTYSPOT) ){
		    		
		    		Point newLocation = new Point(startLocation.x + 1, startLocation.y + 1);
		    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
	    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
	    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
	    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
	    	        
	    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
	    	        
	    	        if(aiIsOnGrid(fwdTarPt)){
	    	        	
	    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
	    	        	
	    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
		    	        
	    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
	    	        	}
	    	        }
		    		
		    		if(aiIsOnGrid(bkwdTarPt)){
		    			
			    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
			    		
			    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
			    			
			    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
			        	}
		    		}
		    	}  		
	    		//Case 6 ... x + 1, y - 1
	    		if( aiIsOnGrid(new Point(startLocation.x + 1, startLocation.y - 1)) 
		    	&& (gameState[startLocation.x][startLocation.y-2] == EMPTYSPOT) ){
		    		
		    		Point newLocation = new Point(startLocation.x + 1, startLocation.y - 1);
		    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
	    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
	    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
	    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
	    	        
	    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
	    	        
	    	        if(aiIsOnGrid(fwdTarPt)){
	    	        	
	    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
	    	        	
	    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
		    	        
	    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
	    	        	}
	    	        }
		    		
		    		if(aiIsOnGrid(bkwdTarPt)){
		    			
			    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
			    		
			    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
			    			
			    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
			        	}
		    		}
		    	}
	    		//Case 7 ... x - 1, y + 1
	    		if( aiIsOnGrid(new Point(startLocation.x - 1, startLocation.y + 1)) 
		    	&& (gameState[startLocation.x - 2][startLocation.y] == EMPTYSPOT) ){
		    		
		    		Point newLocation = new Point(startLocation.x - 1, startLocation.y + 1);
		    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
	    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
	    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
	    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
	    	        
	    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
	    	        
	    	        if(aiIsOnGrid(fwdTarPt)){
	    	        	
	    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
	    	        	
	    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
		    	        
	    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
	    	        	}
	    	        }
		    		
		    		if(aiIsOnGrid(bkwdTarPt)){
		    			
			    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
			    		
			    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
			    			
			    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
			        	}
		    		}
		    	}
	    		//Case 8 ... x - 1, y - 1
	    		if( aiIsOnGrid(new Point(startLocation.x - 1, startLocation.y - 1)) 
		    	&& (gameState[startLocation.x - 2][startLocation.y - 2] == EMPTYSPOT) ){
		    		
		    		Point newLocation = new Point(startLocation.x - 1, startLocation.y - 1);
		    		Point fwd = Vector.subtract(newLocation,startLocation); //forward
	    	        Point fwdTarPt = Vector.add(newLocation,fwd); //target point
	    	        Point bkwd = Vector.subtract(startLocation,newLocation); //backward
	    	        Point bkwdTarPt = Vector.add(startLocation,bkwd); //target point
	    	        
	    	        movingColor = gameState[startLocation.x-1][startLocation.y-1];
	    	        
	    	        if(aiIsOnGrid(fwdTarPt)){
	    	        	
	    	        	fwdColor = gameState[fwdTarPt.x-1][fwdTarPt.y-1]; 
	    	        	
	    	        	if( (movingColor != fwdColor) && (fwdColor != EMPTYSPOT) ){	    			
		    	        
	    	        		validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
	    	        	}
	    	        }
		    		
		    		if(aiIsOnGrid(bkwdTarPt)){
		    			
			    		bkwdColor = gameState[bkwdTarPt.x-1][bkwdTarPt.y-1];
			    		
			    		if( (movingColor != bkwdColor) && (bkwdColor != EMPTYSPOT) ){
			    			
			    			validMoves.add(new Move(startLocation.x, startLocation.y, newLocation.x, newLocation.y) );
			        	}
		    		}
		    	}  		
	    	}    		    	
	    }    
	    return validMoves;
    
    }//}}}
	
    int[][] alphaBetaSearch(int[][] gameState){

    	int bestMove = maxValue(gameState,5,-99999,99999);

    	return null;
    } 


    int maxValue(int[][] gameState, int depth, int alpha, int beta){


        if ( gameOver(gameState) || depth <= 0) return evaluateBoard(gameState);
		
        int bestMove = -99999;
        int [][] previousBoard; 
        ArrayList<Move> validMoves = getValidMoves(gameState);

        for (Move move: validMoves){
        	
        	
        	gameState = move.makeMove(gameState);
        	move.setValue( evaluateBoard(gameState) );
            bestMove = Math.max(bestMove,minValue(gameState, depth-1, alpha, beta));	        			
            alpha = Math.max(alpha, bestMove);
            
            if (bestMove >= beta) return bestMove;
        }
        return bestMove;
    }

    int minValue(int[][] gameState, int depth, int alpha, int beta){


        if ( gameOver(gameState) || depth <= 0 ) return evaluateBoard(gameState);

        int bestMove = 99999;
        ArrayList<Move> validMoves = getValidMoves(gameState);

        for (Move move: validMoves){
        	
        	gameState = move.makeMove(gameState);
        	move.setValue( evaluateBoard(gameState) );
            bestMove = Math.min(bestMove, maxValue(gameState, depth-1, alpha, beta));
            beta = Math.min(beta, bestMove);
	    
            if (bestMove <= alpha) return bestMove;
        }
        return bestMove;
    }

    public Move getMove(int[][] gridState) {
        //returns a Move which is 2 pts: start & end
    	ArrayList<Move> allMoves = getValidMoves(gridState);
    	Move bestMove = allMoves.get(0);

    	System.out.println("AI.JAVA " + bestMove.startPointX + " " + bestMove.startPointY);
    	System.out.println("AI.JAVA " + bestMove.endPointX + " " + bestMove.endPointY);
    	
        return bestMove;
    }

    static public Move getDoubleMove(int[][] gridState, List<Point> validEndPts) {
        //only need the end point & the direction
        return new Move(0,0,0,0);
    }
}
