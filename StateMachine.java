import java.awt.Point;
import java.util.*;

//Purpose: Takes ambiguous state-dependent user actions & decides what to do
//TODO somebody: block state change during animation (real thread blocking)
public class StateMachine {
    public Grid grid;
    private State s;
    //private int win; // 0 - playing, 1 - win, 2 - lose
    public int movesRemaining;
    private int MAX_MOVES = 50;

    //data valid only in certain states
    private Piece selectedPiece;
    java.util.List<Point> prevPositions;
    int prevDirection; //NEWS -> 1234, ignoring diagonals for now, 0=none

    //need to run() with a "NewGame" event before anything will happen
    public StateMachine() {//{{{
        setState(State.GAME_OVER);
        //selectedPiece is not valid until MOVE states
		grid = new Grid();  
		//win = 0;
        movesRemaining = MAX_MOVES;
    }//}}}

    public State getState() { return s; }

    public String run(String evtType, Point p) {//{{{
        //advance the state
        if(evtType == "GameOver") {
            setState(State.GAME_OVER);
        } else if (evtType == "NewGame") {
            newGame();
            setState(State.PLAYER_SELECT);
        } else if(evtType == "Click") {
            handleClick(p);
        }
        return messageForCurrentState();
    }//}}}

    private String messageForCurrentState() {//{{{
        String turnMsg = "Turn #" + (MAX_MOVES - movesRemaining) + " ";
        switch(s) {
            case PLAYER_SELECT:
                return turnMsg + "STATE: PLAYER_SELECT - White, please select a piece";
            case ENEMY_SELECT:
                return turnMsg + "STATE: ENEMY_SELECT - Black, please select a piece";
            case MOVE:
                return turnMsg + "STATE: MOVE - Please move your selected piece";
            case MOVE_AGAIN:
                return turnMsg + "STATE: MOVE_AGAIN - Please move the same piece again. Currently, the decline functionality has not been implemented.";
            case GAME_OVER:
                return "STATE: GAME_OVER - To play again, please reset the board with the NEW_GAME button.";
        }
        return "ERROR: Reached invalid state";
    }//}}}

    private void setState(State newState) {//{{{
        //code called on ending a turn
        if((newState == State.PLAYER_SELECT) || (newState == State.ENEMY_SELECT)) {
            clearTempData();
            if(movesRemaining <= 0) { 
                grid.loseMessage();
                this.run("GameOver", null);
                return;
            }
            movesRemaining--;
        }

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
        clearTempData();
        movesRemaining = MAX_MOVES;
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
        }
    }//}}}

    private void clearTempData() {//{{{
        selectedPiece = null;
        prevPositions = new ArrayList<Point>();
        prevDirection = 0;
    }//}}}

    private void selectPiece(Point pt) {//{{{
        selectedPiece = grid.getPieceAt(pt);
        selectedPiece.highlight();
        grid.repaint();
    }//}}}

    private void movePiece(Point pt) {//{{{
        selectedPiece.unhighlight();
        grid.repaint(); //technically redundant as you movePiece soon

        Point oldPt = selectedPiece.position();
        prevPositions.add(oldPt);
        prevDirection = getDirection(oldPt, pt);
        grid.movePiece(selectedPiece.position(), pt);
    }//}}}

    //TODO somebody - write it
    private int getDirection(Point a, Point b) { return 0; }

    //private void gameFinished() {
    //	if(win == 1)
    //		grid.winMessage();
    //	else if(win == 2)
    //		grid.loseMessage();
    //	else;
    //}
}
