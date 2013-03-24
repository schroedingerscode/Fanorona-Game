import java.awt.Point;
import java.util.*;

//Purpose: Takes ambiguous state-dependent user actions & decides what to do
//TODO somebody: block state change during animation (real thread blocking)
public class StateMachine {
    public Grid grid;
    private State s;
    private int win; // 0 - playing, 1 - win, 2 - lose

    //data valid only in certain states
    private Piece selectedPiece;
    java.util.List<Point> prevPositions;
    int prevDirection; //NEWS -> 1234, ignoring diagonals for now, 0=none

    //need to run() with a "NewGame" event before anything will happen
    public StateMachine() {//{{{
        setState(State.GAME_OVER);
        //selectedPiece is not valid until MOVE states
		grid = new Grid();  
		win = 0;
    }//}}}

    public State getState() { return s; }

    public void run(String evtType, Point p) {//{{{
        //advance the state
        if(evtType == "GameOver") {
            setState(State.GAME_OVER);
        } else if (evtType == "NewGame") {
            newGame();
            setState(State.PLAYER_SELECT);
        } else if(evtType == "Click") {
            handleClick(p);
        }
    }//}}}

    private void setState(State newState) {//{{{
        //TODO NAM <insert code> - change message box text
        s = newState;
    }//}}}

    private void handleClick(Point globalPt) {//{{{
        //get clicked pt in grid coordinates
        Point pt = Grid.asGridCoor(globalPt);
        if(!grid.isOnGrid(pt)) { return; }
        //figure out if it is a space:0, ally piece:1, or enemy:-1
        int id = grid.getState()[pt.x][pt.y];

        switch(s) {
            case PLAYER_SELECT:
                if(id == 1) { 
                    selectPiece(pt);
                    setState(State.MOVE);
                } //else do nothing
                break;
            case ENEMY_SELECT:
                if(id == -1) { 
                    selectPiece(pt);
                    setState(State.MOVE);
                } //else do nothing
                break;
            case MOVE:
                if(id == 0) {
                    if (grid.isValidMove(selectedPiece.position(), pt)) {
                        this.movePiece(pt);
                        handleChainedMove(); //sets next state
                    } //else do nothing
                }
                break;
            case MOVE_AGAIN:
                if(id == 0) {
                    if(grid.isValidDoubleMove(selectedPiece.position(), pt, prevPositions, prevDirection)) {
                        this.movePiece(pt);
                        handleChainedMove(); //sets next state
                    }
                    //no option to cancel or decline to move yet
                }
                break;
        }
    }//}}}

    private void newGame() {//{{{
        grid.reset();
    }//}}}

    private void handleChainedMove() {//{{{
        if(grid.canMoveAgain(selectedPiece.position(), prevPositions, prevDirection)) {
            setState(State.MOVE_AGAIN); 
        } else {
            if(selectedPiece.isPlayer()) {
                setState(State.ENEMY_SELECT);
            } else {
                setState(State.PLAYER_SELECT);
            }
            clearTempData();
        }
    }//}}}

    private void clearTempData() {//{{{
        selectedPiece = null;
        prevPositions = new ArrayList<Point>();
        prevDirection = 0;
    }//}}}

    //TODO somebody - highlight code
    private void selectPiece(Point pt) {//{{{
        //TODO highlight piece
        selectedPiece = grid.getPieceAt(pt);
        selectedPiece.highlight();
    }//}}}

    private void movePiece(Point pt) {//{{{
        selectedPiece.unhighlight();

        Point oldPt = selectedPiece.position();
        prevPositions.add(oldPt);
        prevDirection = getDirection(oldPt, pt);
        grid.movePiece(selectedPiece.position(), pt);
    }//}}}

    //TODO somebody - write it
    private int getDirection(Point a, Point b) { return 0; }

    private void gameFinished()
    {
    	if(win == 1)
    		grid.winMessage();
    	else if(win == 2)
    		grid.loseMessage();
    	else;
    }
}
