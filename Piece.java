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

    ////Images swapped out highlighted/normal
    //public void highlight() 
    //{
    //    String fileName = (color?"wpiece_highlighted.png":"bpiece_highlighted.png");
    //    ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
    //    piecePic = ii.getImage();

    //    System.out.println("Highlighted");
    //}
    //
    //public void unhighlight() 
    //{
    //	String fileName = (color?"wpiece.png":"bpiece.png");
    //    ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
    //    piecePic = ii.getImage();
    //}

    public void move(Point destination)
    {
    	x = destination.x;
    	y = destination.y;
    }
    
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
            g2d.drawImage(highlight, globalX - 50, globalY - 50, null);
        }
        //draw the actual piece image
        //x+1 for border, -45 to center 90x90 image
        g2d.drawImage(piecePic, globalX - 45, globalY - 45, null);
    }//}}}
}
