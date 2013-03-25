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

        System.out.println("Clicked: " + pt.x + ", " + pt.y);
        
        switch(s) {
            case PLAYER_SELECT:
                if(id == 1) { 
                	System.out.println("PLAYER_SELECT");
                    selectPiece(pt);
                    setState(State.MOVE);
                } //else do nothing
                break;
            case ENEMY_SELECT:
                if(id == -1) { 
                	System.out.println("ENEMY_SELECT");
                    selectPiece(pt);
                    setState(State.MOVE);
                } //else do nothing
                break;
            case MOVE:
                if(id == 0) {
//                    if (grid.isValidMove(selectedPiece.position(), pt)) {
                    	System.out.println("MOVE");
//                        this.movePiece(pt);
                        handleChainedMove(); //sets next state
//                    } //else do nothing
                }
                break;
            case MOVE_AGAIN:
                if(id == 0) {
                    if(grid.isValidDoubleMove(selectedPiece.position(), pt, prevPositions, prevDirection)) {
                    	System.out.println("MOVE AGAIN");
                        this.movePiece(pt);
                        handleChainedMove(); //sets next state
                    }
                    //no option to cancel or decline to move yet
                }
                break;
            case GAME_OVER:
            	if(win == 1)
            		grid.winMessage();
            	else if(win == 2)
            		grid.loseMessage();
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

    //0-7 (assuming points are adjacent)
    //0: N, 1: NE, 2: E, 3: SE, 
    //4: S, 5: SW, 6: W, 7: NW 
    private int getDirection(Point a, Point b) { 
    	if(a.x == b.x && a.y - 1 == b.y)
    		return 0;
    	else if(a.x + 1 == b.x && a.y - 1 == b.y)
    		return 1;
    	else if(a.x + 1 == b.x && a.y == b.y)
    		return 2;
    	else if(a.x + 1 == b.x && a.y + 1 == b.y)
    		return 3;
    	else if(a.x == b.x && a.y + 1 == b.y)
    		return 4;
    	else if(a.x - 1 == b.x && a.y + 1 == b.y)
    		return 5;
    	else if(a.x - 1 == b.x && a.y == b.y)
    		return 6;
    	else if(a.x - 1 == b.x && a.y - 1 == b.y)
    		return 7;
    	else
    		return -1; 
    }
    
    private Point getPointInDirection(Point a, int direction) {
    	Point p;
    	switch(direction) {
    		case 0: 
    			p = new Point(a.x, a.y-1);
    			break;
    		case 1:
    			p = new Point(a.x+1, a.y-1);
    			break;
    		case 2:
    			p = new Point(a.x+1, a.y);
    			break;
    		case 3: 
    			p = new Point(a.x+1, a.y+1);
    			break;
    		case 4:
    			p = new Point(a.x, a.y+1);
    			break;
    		case 5: 
    			p = new Point(a.x-1, a.y+1);
    			break;
    		case 6:
    			p = new Point(a.x-1, a.y);
    			break;
    		case 7:
    			p = new Point(a.x-1, a.y-1);
    			break;
    		default:
    			p = null;
    			break;
    	}    	
    	return p;
    }
}
