//Data & UI
import java.awt.*;
import javax.swing.*;

//draws directly onto the grid, need not extend JPanel
//would prefer it to draw onto itself & be added to JPanel though
public class Piece {
    private int x = 0;
    private int y = 0;
    private int W = 100; //piece width
    private int H = 100; //piece height
    private Boolean color = false; //0 = blk, 1 = white

    public Point position() { return new Point(x,y); }

    Piece(Point pt, Boolean color) { 
        x = pt.x;
        y = pt.y;
        initPiece(color);
    }

    Piece(int _x, int _y, Boolean color) { 
        x = _x;
        y = _y;
        initPiece(color);
    }

    private void initPiece(Boolean _color) {
        //meat of the constructor, shared between the 2
        color = _color;
    }

    public void drawPiece(Graphics g) {
        //called by the grid, which passes itself in
        //drawing directly to grid as adding a canvas on top was problematic
        int b = 55; //border 55,55,90,90
        int f = 58; //fill 58,58,84,84 white

        //black circle, for white, will serve as border when mostly covered
        g.setColor(Color.BLACK);
        g.fillOval(Grid.SQ_W*x+b, Grid.SQ_H*y+b, 90, 90);

        //smaller white circle on top if a white piece
        if(color) { //is white
            g.setColor(Color.WHITE);
            g.fillOval(Grid.SQ_W*x+f, Grid.SQ_H*y+f, 84, 84);
        } //else already done
    }
}
