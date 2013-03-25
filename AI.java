import java.awt.Point;
import java.util.ArrayList;

public class AI {
    AI() {}
	
    static Boolean gameOver(int[][] gameState){
	// Place holder, code written just have to meld things together
	// during our next meeting
	return false;
	
    }

    static int evaluateBoard(int[][] gameState){
	//Insert evaluate code here
	return 1;

    }

    static ArrayList<int[][]> getValidMoves(int[][] gameState) {
	//Place holder
	return null;
    }
	
    static int[][] alphaBetaSearch(int[][] gameState){

    	int bestMove = maxValue(gameState,5,-99999,99999);

	return null;
    } 


    static int maxValue(int[][] gameState, int depth, int alpha, int beta){


        if ( gameOver(gameState) || depth <= 0) return evaluateBoard(gameState);
		
        int bestMove = -99999;
	ArrayList<int[][]> validMoves = getValidMoves(gameState);

        for (int[][] move: validMoves){

            bestMove = Math.max(bestMove,minValue(move, depth-1, alpha, beta));	        			
            alpha = Math.max(alpha, bestMove);
            
            if (bestMove >= beta) return bestMove;
        }
        return bestMove;
    }

    static int minValue(int[][] gameState, int depth, int alpha, int beta){


        if ( gameOver(gameState) || depth <= 0 ) return evaluateBoard(gameState);

        int bestMove = 99999;
	ArrayList<int[][]> validMoves = getValidMoves(gameState);

        for (int[][] move: validMoves){

            bestMove = Math.min(bestMove, maxValue(move, depth-1, alpha, beta));
	    beta = Math.min(beta, bestMove);
	    
            if (bestMove <= alpha) return bestMove;
        }
        return bestMove;
    }

    public ArrayList<Point> getMove(int[][] gridState) {
        //returns a list of points (grid coor) specifying the move
        //1 - selected piece
        //2 - space to move to
        //3,4 - repeated move
        // repeat as necessary
        return new ArrayList<Point>();
    }
}
