//The (9x5) game board class which stores references to all pieces
//and manages their positions.
//The y-axis is inverted so (0,0) is in the top left.
//The x-axis is normal.

import java.awt.Point;
import java.util.*;

public class Grid {
    private List<Piece> playerPieces;
    private List<Piece> enemyPieces;
    private int MIN_GRID_WIDTH_INDEX = 0;
    private int MAX_GRID_WIDTH_INDEX = 8;
    private int MIN_GRID_HEIGHT_INDEX = 0;
    private int MAX_GRID_HEIGHT_INDEX = 4;

    //!!!WARNING UNTESTED
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
        if(!isValidPosition(coor)) { return false; }

        if(isAlly) {
            playerPieces.add(new Piece(coor));
        } else {
            enemyPieces.add(new Piece(coor));
        }
        return true;
    }

    private void clearGrid() {
        playerPieces = new ArrayList<Piece>();
        enemyPieces = new ArrayList<Piece>();
    }

    //!!!WARNING UNTESTED
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

    public void resetGrid() {
        clearGrid();
        placePiecesInInitBoardState();
    }

    public Grid() { resetGrid(); }
}
