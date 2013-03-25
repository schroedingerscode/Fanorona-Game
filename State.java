public enum State {
    //2 FSM inputs - run(a,b)
    //A) EVENT String - (click, specific button, gameover)
    //B) (clicks-only) - transitions depend on what was clicked
    PLAYER_SELECT,
    ENEMY_SELECT,       //enemy (human or AI) needs to select a piece
    MOVE,         //enemy needs to move, inputs - fwd/bk & where to
    MOVE_AGAIN,   //may or may not get here depending on output
    GAME_OVER
}
