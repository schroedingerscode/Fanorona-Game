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

    public Point position() { return new Point(x,y); }

    public Boolean isPlayer() { return color; } //white = T

    //Images swapped out highlighted/normal
    public void highlight() 
    {
        String fileName = (color?"wpiece_highlighted.png":"bpiece_highlighted.png");
        ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
        piecePic = ii.getImage();

        System.out.println("Highlighted");
    }
    
    public void unhighlight() 
    {
    	String fileName = (color?"wpiece.png":"bpiece.png");
        ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
        piecePic = ii.getImage();
    }

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
        String fileName = (colorIsWhite?"wpiece.png":"bpiece.png");
        ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
        piecePic = ii.getImage();
    }

    public void drawPiece(Graphics2D g2d) {//{{{
        //x+1 for border, -45 to center 90x90 image
        g2d.drawImage(piecePic, Grid.SQ_W*(x+1)-45, Grid.SQ_H*(y+1)-45, null);
    }//}}}
}
