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
    Boolean color;

    Image piecePic;
    Image highlight;
    Boolean isHighlighted;
    
    double resizeFactor;

    public Point position() { return new Point(x,y); }

    public Boolean isPlayer() { return color; } //white = true

    public void highlight() {
        //must call grid.repaint() after returning from here
        isHighlighted = true; 
    }

    public void unhighlight() {
        //must call grid.repaint() after returning from here
        isHighlighted = false; 
    }

    public void move(Point destination) {
        x = destination.x;
    	y = destination.y;
    }
    
    Piece(Point pt, Boolean color, double changeFactor) { 
        x = pt.x;
        y = pt.y;
        initPiece(color);
        resizeFactor = changeFactor;
    }

    Piece(int _x, int _y, Boolean color, double changeFactor) { 
        x = _x;
        y = _y;
        initPiece(color);
        resizeFactor = changeFactor;
    }

    private void initPiece(Boolean colorIsWhite) {
        //meat of the constructor, shared between the two
        color = colorIsWhite;
        isHighlighted = false;
        //load piece image
        String fileName = (colorIsWhite?"wpiece.png":"bpiece.png");
        ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
        piecePic = ii.getImage();
        //load highlight image
        ImageIcon ii2 = new ImageIcon(this.getClass().getResource("Selector.png"));
        highlight = ii2.getImage();
    }

    public void drawPiece(Graphics2D g2d) {//{{{
        int globalX = Grid.SQ_W*(x+1);
        int globalY = Grid.SQ_H*(y+1);
        //add the highlight image below the piece-to-be-drawn, -50 to center
        if(isHighlighted) {
            g2d.drawImage(highlight, globalX - ((int)(50*resizeFactor)), globalY - ((int)(50*resizeFactor)), (int)(highlight.getWidth(null)*resizeFactor), (int)(highlight.getHeight(null)*resizeFactor), null);
        }
        //draw the actual piece image
        //x+1 for border, -45 to center 90x90 image
        //FIX THIS SHITJOIAFJAJF)(FU)**H)(@#R
        g2d.drawImage(piecePic, globalX - ((int)(45*resizeFactor)), globalY - ((int)(45*resizeFactor)), (int)(piecePic.getWidth(null)*resizeFactor), (int)(piecePic.getHeight(null)*resizeFactor), null);
    }//}}}
}
