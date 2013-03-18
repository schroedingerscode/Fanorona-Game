import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Fanorona extends JPanel
{
	public static void main(String[] args) 
	{
		JFrame window = new JFrame("Fanorona");
		Fanorona content = new Fanorona();
		window.setContentPane(content);
		window.pack();
		window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		window.setResizable(false);  
		window.setVisible(true);
	}
	
	private JButton newGameButton;  
	private JButton instructionsButton; 
	private JButton nameButton;
	private JLabel message;  
   
	public Fanorona() 
	{
		setLayout(null);  
		setPreferredSize(new Dimension(1000,650));
		      
		Grid grid = new Grid();  
		add(grid);
		add(newGameButton);
		add(instructionsButton);	
		add(nameButton);
		add(message);
		      		      
		grid.setBounds(1,51,1000,600); 
		newGameButton.setBounds(10, 10, 120, 30);
		instructionsButton.setBounds(140, 10, 120, 30);
		nameButton.setBounds(870, 10, 120, 30);
		message.setBounds(300, 10, 350, 30);
	} 
	
	private static class FanoronaMove
	{
		int fromX, fromY, toX, toY;
		
		FanoronaMove(int x0, int y0, int x1, int y1)
		{
			fromX = x0;
			fromY = y0;
			toX = x1;
			toY = y1;	
		}
		
		// not done		
	}
	
	private class Grid extends JPanel implements ActionListener, MouseListener 
	{      
		FanoronaData grid;  
		      
		boolean playing; 
		      
		int currentPlayer; // NORTH_PLAYER / SOUTH_PLAYER
		
		String playerName;
		
		int selectedRow, selectedCol;  
		
		FanoronaMove[] validMoves;  
		
		Grid() 
		{
			setBackground(Color.BLACK);
			addMouseListener(this);
			instructionsButton = new JButton("Instructions");
			instructionsButton.addActionListener(this);
			newGameButton = new JButton("New Game");
			newGameButton.addActionListener(this);
			nameButton = new JButton("Change Name");
			nameButton.addActionListener(this);
			message = new JLabel("",JLabel.CENTER);
			message.setFont(new  Font("Serif", Font.BOLD, 14));
			message.setForeground(Color.BLACK);
			grid = new FanoronaData();
			newGame();
		}

		public void actionPerformed(ActionEvent evt) 
		{
			Object src = evt.getSource();
			if(src == newGameButton)
				newGame();
			else if(src == instructionsButton)
				instructions();
			else if(src == nameButton)
				changeName();
		}
	
		// new game
		void newGame() 
		{
			System.out.println("New game should be started");
			if (playing == true) 
			{
				message.setText("Current game is not finished.");
				return;
			}
			grid.setUpGame();   // Set up the pieces.
			currentPlayer = FanoronaData.SOUTH_PLAYER;   
			validMoves = grid.getValidMoves(FanoronaData.SOUTH_PLAYER);  
			selectedRow = -1;   
			message.setText("South Player:  Make your move.");
			playing = true;
	//		newGameButton.setEnabled(false);
	//		instructionsButton.setEnabled(true);
			repaint();
		}		      
	      
		//instructions for Fanorona
		void instructions() 
		{
			String instructionDialog = "From Wikipedia:\n" +
					"* Players alternate turns, starting with White.\n" +
					"* We distinguish two kinds of moves, non-capturing and capturing moves. A non-capturing move is called a paika move.\n" +
					"* A paika move consists of moving one stone along a line to an adjacent intersection.\n" +
					"* Capturing moves are obligatory and have to be played in preference to paika moves.\n" +
					"* Capturing implies removing one or more pieces of the opponent. It can be done in two ways, either (1) by approach or (2) by withdrawal.\n" +
					"  (1) An approach is moving the capturing stone to a point adjacent to an opponent stone provided that the stone is on the continuation of the capturing stone's movement line.\n" +
					"  (2) A withdrawal works analogously to an approach except that the movement is away from the opponent stone.\n" +
					"* When an opponent stone is captured, all opponent pieces in line behind that stone (as long as there is no interruption by an empty point or an own stone) are captured as well.\n" +
					"* If a player can do an approach and a withdrawal at the same time, he has to choose which one he plays.\n" +
					"* As in checkers, the capturing piece is allowed to continue making successive captures, with these restrictions:\n" +
					"  (1) The piece is not allowed to arrive at the same position twice.\n" +
					"  (2) It is not allowed to move a piece in the same direction as directly before in the capturing sequence. This can happen if an approach follows on a withdrawal.\n" +
					"* The game ends when one player captures all stones of the opponent. If neither player can achieve this, the game is a draw.\n";
			JOptionPane.showMessageDialog(this, instructionDialog, "Fanorona Instructions", JOptionPane.PLAIN_MESSAGE);
		}

		void changeName()
		{
			playerName = JOptionPane.showInputDialog(null, "Enter player name: ", "", JOptionPane.PLAIN_MESSAGE);
			message.setText("Player name is: " + playerName);
		}
		
		void gameOver(String str) 
		{
			message.setText(str);
			newGameButton.setEnabled(true);
			instructionsButton.setEnabled(false);
			playing = false;
		}

		void doClick(int x, int y) 
		{
//			for (int i = 0; i < validMoves.length; i++)
//			{
//				if (validMoves[i].fromX == x && validMoves[i].fromY == y) 
//				{
//					selectedRow = x;
//					selectedCol = y;
//					if (currentPlayer == FanoronaData.SOUTH_PLAYER)
//						message.setText("RED:  Make your move.");
//					else
//						message.setText("BLACK:  Make your move.");
//					repaint();
//					return;
//				}
//			}

			if (selectedRow < 0) 
			{
				message.setText("Click the piece you want to move.");
				return;
			}

			for (int i = 0; i < validMoves.length; i++)
			{
				if (validMoves[i].fromX == selectedRow && validMoves[i].fromY == selectedCol && validMoves[i].toX == x && validMoves[i].toY == y) 
				{
					doMakeMove(validMoves[i]);
					return;
				}
			}
			message.setText("Click the square you want to move to.");
		}

		void doMakeMove(FanoronaMove move) 
		{         
			grid.makeMove(move);
			         
			if (currentPlayer == FanoronaData.SOUTH_PLAYER) 
			{
				currentPlayer = FanoronaData.NORTH_PLAYER;
				validMoves = grid.getValidMoves(currentPlayer);
				if (validMoves == null)
					gameOver("NORTH_PLAYER has no moves.  SOUTH_PLAYER wins.");
				else
					message.setText("NORTH_PLAYER:  Make your move.");
			}
			else 
			{
				currentPlayer = FanoronaData.SOUTH_PLAYER;
				validMoves = grid.getValidMoves(currentPlayer);
				if (validMoves == null)
					gameOver("SOUTH_PLAYER has no moves.  NORTH_PLAYER wins.");
				else
					message.setText("SOUTH_PLAYER:  Make your move.");
			}

			selectedRow = -1;

			if (validMoves != null)
			{
				boolean sameStartSquare = true;
				for (int i = 1; i < validMoves.length; i++)
				{
					if (validMoves[i].fromX != validMoves[0].fromX || validMoves[i].fromY != validMoves[0].fromY) 
					{
						sameStartSquare = false;
						break;
					}
					if (sameStartSquare) 
					{
						selectedRow = validMoves[0].fromX;
						selectedCol = validMoves[0].fromY;
					}
				}
			}			
			repaint();
		}       
     
		public void paintComponent(Graphics g)
		{					
			//drawing the grid instead
			g.drawRect(0,0,getSize().width-1,getSize().height-1);
						
			for(int x = 0; x < 9; x++)
			{
				for(int y = 0; y < 5; y++)
				{				
					g.drawLine(100*x+101, 100, 100*x+101, 500);
					g.drawLine(100*x+100, 100, 100*x+100, 500);
					g.drawLine(100*x+99, 100, 100*x+99, 500);
				
					g.drawLine(100, 100*y+101, 900, 100*y+101);
					g.drawLine(100, 100*y+100, 900, 100*y+100);
					g.drawLine(100, 100*y+99, 900, 100*y+99);
							
					g.drawLine(100, 300, 300, 500);
					g.drawLine(100, 100, 500, 500);
					g.drawLine(300, 100, 700, 500);
					g.drawLine(500, 100, 900, 500);
					g.drawLine(700, 100, 900, 300);
					
					g.drawLine(100, 300, 300, 100);
					g.drawLine(100, 500, 500, 100);
					g.drawLine(300, 500, 700, 100);
					g.drawLine(500, 500, 900, 100);
					g.drawLine(700, 500, 900, 300);
				}
			}
					
			//draw pieces
			int count = 0;
			for(int x = 0; x < 9; x++)
			{
				for(int y = 0; y < 5; y++)
				{
					if(y < 2)
					{
			//			g.setColor(Color.RED);
			//			g.fillOval(100*x+55, 100*y+55, 90, 90);
						g.setColor(Color.BLACK);
						g.fillOval(100*x+58, 100*y+58, 84, 84);
					}
					else if(y == 2)
					{
						if(count == 0 || count == 2 || count == 5 || count == 7)
						{
				//			g.setColor(Color.RED);
				//			g.fillOval(100*x+55, 100*y+55, 90, 90);
							g.setColor(Color.BLACK);
							g.fillOval(100*x+58, 100*y+58, 84, 84);
						}			
						if(count == 1 || count == 3 || count == 6 || count == 8)
						{
							g.setColor(Color.BLACK);
							g.fillOval(100*x+55, 100*y+55, 90, 90);
							g.setColor(Color.WHITE);
							g.fillOval(100*x+58, 100*y+58, 84, 84);
						}
					}
					else
					{
						g.setColor(Color.BLACK);
						g.fillOval(100*x+55, 100*y+55, 90, 90);
						g.setColor(Color.WHITE);
						g.fillOval(100*x+58, 100*y+58, 84, 84);
					}					
				}
				count++;
			}
				
			if (playing) 
			{
				g.setColor(Color.cyan);
				
				if (selectedRow >= 0) 
				{
					message.setText("selected row: " + selectedRow);
				}
			}
		}  
	      
		public void mousePressed(MouseEvent click) 
		{
			message.setText("Location clicked: " + click.getX() + ", " + click.getY());
			if (playing == false)
				message.setText("Click \"New Game\" to start a new game.");
			else 
			{
				//formula for determing circle clicked
//				int x = (click.getX() - 2) / 20;
//				int y = (click.getY() - 2) / 20;
//				if (x >= 0 && x < 9 && y >= 0 && y < 5)
//					doClick(x,y);
				
				// ON THIS PART A2089RJ0QFJQ39JQ309J093409RQ3)(!*!)!@#*(
			}
		}
	      	      
		public void mouseReleased(MouseEvent evt) { }
		public void mouseClicked(MouseEvent evt) { }
		public void mouseEntered(MouseEvent evt) { }
		public void mouseExited(MouseEvent evt) { }
	}
	
	private static class FanoronaData
	{
		static final int EMPTY = 0;
		static final int SOUTH_PLAYER = 1;
		static final int NORTH_PLAYER = 2;
		
		int[][] grid;
		
		FanoronaData()
		{
			grid = new int[9][5];
			setUpGame();
		}
		
		void setUpGame()
		{
			int count = 0;
			for(int x = 0; x < 9; x++)
			{
				for(int y = 0; y < 5; y++)
				{
					if(y < 2)
						grid[x][y] = NORTH_PLAYER;
					else if(y == 2)
					{
						if(count == 0 || count == 2 || count == 5 || count == 7)
							grid[x][y] = NORTH_PLAYER;
						if(count == 1 || count == 3 || count == 6 || count == 8)
							grid[x][y] = SOUTH_PLAYER;
						else
							grid[x][y] = EMPTY;
					}
					else if(y > 2)
						grid[x][y] = SOUTH_PLAYER;
					else
						grid[x][y] = EMPTY;
				}			
				count++;
			}
		}
		
		int getPieceAt(int x, int y)
		{
			return grid[x][y];
		}
		
		void makeMove(FanoronaMove move)
		{
	         makeMove(move.fromX, move.fromY, move.toX, move.toY);
		}
		
		void makeMove(int fromX, int fromY, int toX, int toY)
		{
			grid[toX][toY] = grid[fromX][fromY];
			grid[fromX][fromY] = EMPTY;
			
			// other logic
		}
		
		FanoronaMove[] getValidMoves(int player)
		{
			ArrayList<FanoronaMove> moves = new ArrayList<FanoronaMove>();	
			
			if(player != NORTH_PLAYER || player != SOUTH_PLAYER)
				return null;
			
			//lots of stuff here
			if(moves.size() == 0)
			{
				for(int x = 0; x < 9; x++)
				{
					for(int y = 0; y < 5; y++)
					{
						if(grid[x][y] == player)
						{
							if (moveable(player,x,y,x+1,y+1))
								moves.add(new FanoronaMove(x,y,x+1,y+1));
							if (moveable(player,x,y,x-1,y+1))
								moves.add(new FanoronaMove(x,y,x-1,y+1));
							if (moveable(player,x,y,x+1,y-1))
								moves.add(new FanoronaMove(x,y,x+1,y-1));
							if (moveable(player,x,y,x-1,y-1))
								moves.add(new FanoronaMove(x,y,x-1,y-1));
						}
					}
				}
			}
						
			if(moves.size() == 0)
				return null;
			else
			{
				FanoronaMove[] validMoves = new FanoronaMove[moves.size()];
				for(int n = 0; n < moves.size(); n++)
					validMoves[n] = moves.get(n);
				return validMoves;
			}
		}
		
		private boolean moveable(int player, int fromX, int fromY, int toX, int toY)
		{
			if(fromX < 0 || fromY < 0 || fromX > 9 || fromY > 5 || toX < 0 || toY < 0 || toX > 9 || toY > 5)
				return false;
			if(grid[toX][toY] != EMPTY)
				return false;
			else
				return true;
			//not done
		}
	}	
}