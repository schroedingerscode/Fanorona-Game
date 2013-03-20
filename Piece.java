//The dumb piece class stores all piece-related data. 
//As of now, it only stores position and may or may not do more in the future.
//Data only, the UI representation is handled by another class.`
import java.awt.Point;

public class Piece extends Point {
    Point position() { return new Point(x,y); }
    Piece(Point pt) { super(pt); }
    Piece(int x, int y) { super(x, y); }
}
