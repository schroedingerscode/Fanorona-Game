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
    Image sacrifice;
    Boolean isHighlighted;
    public Boolean isSacrifice;
    
    Grid g;

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

    public void sacrifice() {
        //must call grid.repaint() after returning from here
        isSacrifice = true; 
    }

    public void move(Point destination) {
        x = destination.x;
    	y = destination.y;
    }
    
    Piece(Point pt, Boolean color, Grid g) { 
        x = pt.x;
        y = pt.y;
        initPiece(color, g);
    }

    Piece(int _x, int _y, Boolean color, Grid g) { 
        x = _x;
        y = _y;
        initPiece(color, g);
    }

    private void initPiece(Boolean colorIsWhite, Grid _g) {
        //meat of the constructor, shared between the two
        g = _g;
        color = colorIsWhite;
        isHighlighted = false;
        isSacrifice = false;
        //load piece image
        String fileName = (colorIsWhite?"wpiece.png":"bpiece.png");
        ImageIcon ii = new ImageIcon(this.getClass().getResource(fileName));
        piecePic = ii.getImage();
        //load highlight image
        ImageIcon ii2 = new ImageIcon(this.getClass().getResource("Selector.png"));
        highlight = ii2.getImage();
        //load sacrifice highlight image
        ImageIcon ii3 = new ImageIcon(this.getClass().getResource("Sacrifice.png"));
        sacrifice = ii3.getImage();
    }

    public void drawPiece(Graphics2D g2d) {//{{{
        double resizeFactor = g.resizeFactor;
        Point globalPt = g.asGlobalCoor(x,y);
        int globalX = globalPt.x;
        int globalY = globalPt.y;
        //add the highlight image below the piece-to-be-drawn
        double fullW = (int)(highlight.getWidth(null)*resizeFactor);
        double fullH = (int)(highlight.getHeight(null)*resizeFactor);
        if(isHighlighted) {
            g2d.drawImage(highlight, globalX, globalY, (int)fullW, (int)fullH, null);
        } else if(isSacrifice) {
            g2d.drawImage(sacrifice, globalX, globalY, (int)fullW, (int)fullH, null);
        }
        //draw the actual piece image
        double pieceW = (piecePic.getWidth(null)*resizeFactor);
        double pieceH = (piecePic.getHeight(null)*resizeFactor);
        g2d.drawImage(piecePic, globalX + (int)((.5*fullW) - (.5*pieceW)), globalY + (int) ((.5*fullH) - (.5*pieceH)), (int)(piecePic.getWidth(null)*resizeFactor), (int)(piecePic.getHeight(null)*resizeFactor), null);
    }//}}}
}
