import java.awt.Point; 

class Vector {
    static public Point add(Point a, Point b) {
        return new Point(b.x + a.x, b.y + a.y);
    }

    static public Point subtract(Point b, Point a) {
        return new Point(b.x - a.x, b.y - a.y);
    }
}
