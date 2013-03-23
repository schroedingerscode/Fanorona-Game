import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Fanorona extends JPanel implements ActionListener, MouseListener {
    StateMachine stateMachine; //contains grid
    //No flags necessary beyond the state machine itself

	private JButton newGameButton;  
	private JButton instructionsButton; 
	private JButton nameButton;
	private JLabel message;  

    String playerName;

	public static void main(String[] args) {//{{{
        //setup game window
		JFrame window = new JFrame("Fanorona");
		Fanorona content = new Fanorona();
		window.setContentPane(content);
		window.pack();
		window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		window.setResizable(false);  
		window.setVisible(true);
	}//}}}

	public Fanorona() {//{{{
		setLayout(null);  
		setPreferredSize(new Dimension(1000,650));

        stateMachine = new StateMachine();
		add(stateMachine.grid);
		stateMachine.grid.setBounds(1,51,1000,600); 

        initButtons();

        //add listeners
        instructionsButton.addActionListener(this);
        newGameButton.addActionListener(this);
        nameButton.addActionListener(this);
        addMouseListener(this);

        stateMachine.run("NewGame", null);
	} //}}}

    public void initButtons() {//{{{
        instructionsButton = new JButton("Instructions");
        newGameButton = new JButton("New Game");
        nameButton = new JButton("Change Name");
        message = new JLabel("",JLabel.CENTER);
        message.setFont(new  Font("Serif", Font.BOLD, 14));
        message.setForeground(Color.BLACK);

		add(newGameButton);
		add(instructionsButton);	
		add(nameButton);
		add(message);

		newGameButton.setBounds(10, 10, 120, 30);
		instructionsButton.setBounds(140, 10, 120, 30);
		nameButton.setBounds(870, 10, 120, 30);
		message.setBounds(300, 10, 350, 30);
    }//}}}

    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}
    public void mousePressed(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt) {}
    public void mouseClicked(MouseEvent evt) {//{{{
        //possibly TODO somebody: right-clicks as cancellation
        stateMachine.run("Click", evt.getPoint());
    }//}}}

    public void actionPerformed(ActionEvent evt) {//{{{
        Object src = evt.getSource();
        if(src == newGameButton) {
            //message.setText("Started a new game. You are white and it is your turn.");
            stateMachine.run("NewGame", null);
        } else if(src == instructionsButton) {
            instructions();
        } else if(src == nameButton) {
            changeName();
        }
    }//}}}

    void changeName() {//{{{
        playerName = JOptionPane.showInputDialog(null, "Enter player name: ", "", JOptionPane.PLAIN_MESSAGE);
        message.setText("Player name is: " + playerName);
    }//}}}

    void instructions() {//{{{
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
    }//}}}
}
