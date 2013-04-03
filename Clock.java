import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
//import java.util.Timer;
//import java.util.TimerTask;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Clock {
	private Timer timer;
    private JLabel timeLabel = new JLabel(" ", JLabel.CENTER);
    private int timeRemaining;
    private int originalTime;
    private int WINDOW_WIDTH = 280;
    private int WINDOW_HEIGHT = 70;
    private boolean timerOff;
    private boolean isGameOver;

    public Clock(int time) {
        JFrame f = new JFrame("Play Clock");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        f.add(timeLabel);
        f.pack();
        f.setAlwaysOnTop(true);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(screenSize.width - WINDOW_WIDTH, 0);
        f.setResizable(false);
        f.setVisible(true);
        
        isGameOver = false;
        
        ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(timeRemaining <= 0) {
            		timeLabel.setText("You're a loser!");
            		timer.stop();
            		isGameOver = true;
            	}
				else
					timeLabel.setText("Time left for this turn (seconds): " + String.valueOf(timeRemaining--));
			}
		};        
        timer = new Timer(1000, listener);
        originalTime = time;
		timeRemaining = time;
		if(time <= 0)
			timerOff = true;
		else
			timerOff = false;
    }     
        
    public void restartTimer() {		
    	timeRemaining = originalTime;
    	isGameOver = false;
    	if(timerOff)
    		timeLabel.setText("Time left for this turn (seconds): OFF");
    	else {
    		timer.setDelay(1000);
    		timer.restart();
    	}
    }
    
    public boolean gameOver() {
    	return isGameOver;
    }
}
